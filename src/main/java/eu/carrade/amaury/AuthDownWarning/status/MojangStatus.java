/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 * 
 * http://amaury.carrade.eu
 * 
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package eu.carrade.amaury.AuthDownWarning.status;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.carrade.amaury.AuthDownWarning.Config;
import fr.zcraft.zlib.tools.PluginLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class MojangStatus
{
	/**  The service returning a JSON with the services status  */
	private final String CHECK_SERVICE_URI;

	/**  IP → Service  */
	private Map<String, Service> services = new ConcurrentHashMap<>();

	/**  The milli-timestamp of the last ping  */
	private Long lastPing = -1l;


	public MojangStatus()
	{
		CHECK_SERVICE_URI = Config.CHECK_SERVICE_URI.get();

		registerService(new Service("Authentication", "authserver.mojang.com", true));
		registerService(new Service("Multiplayer Sessions", "sessionserver.mojang.com", true));

		registerService(new Service("Minecraft Website", "minecraft.net", false));
		registerService(new Service("Mojang Accounts Website", "account.mojang.com", false));
		registerService(new Service("Minecraft Skins", "textures.minecraft.net", false));
		registerService(new Service("Mojang API", "api.mojang.com", false));
	}


	/**
	 * Registers a new service to check.
	 *
	 * Please notice that if this service is not contained in the Mojang check response,
	 * it will never be updated.
	 *
	 * @param service The service.
	 */
	public void registerService(Service service)
	{
		services.put(service.getAddress(), service);
	}


	/**
	 * @return An immutable set containing all the registered services.
	 */
	public Set<Service> getServices()
	{
		Set<Service> sortedServices = new TreeSet<>(new Comparator<Service>() {
			@Override
			public int compare(Service firstOne, Service otherOne)
			{
				if (firstOne.warnIfDown() == otherOne.warnIfDown())
					return firstOne.getName().compareToIgnoreCase(otherOne.getName());

				else if (firstOne.warnIfDown())
					return -1;

				else
					return 1;
			}
		});

		sortedServices.addAll(services.values());

		return ImmutableSet.copyOf(sortedServices);
	}

	/**
	 * @return An immutable set containing all the registered services down/unstable and with warning.
	 */
	public Set<Service> getDownServicesWithWarning()
	{
		ImmutableSet.Builder<Service> downServicesWithWarning = ImmutableSet.builder();

		for (Service service : services.values())
			if (service.warnIfDown() && (service.getStatus() == Status.UNSTABLE || service.getStatus() == Status.DOWN))
				downServicesWithWarning.add(service);

		return downServicesWithWarning.build();
	}

	/**
	 * @return The date of the last Mojang ping
	 */
	public Date getLastPingDate()
	{
		return new Date(lastPing);
	}


	/**
	 * Checks the services status. Run this asynchronously as it sends network requests.
	 */
	public void updateStatus()
	{
		JsonArray jsonCheck = getCheckRawContent();
		if (jsonCheck == null)
			return;

		for (JsonElement status : jsonCheck)
		{
			if (status.isJsonObject())
			{
				// We only use the first one, as the format is this one.
				final Iterator<Map.Entry<String, JsonElement>> iterator = status.getAsJsonObject().entrySet().iterator();
				if (!iterator.hasNext())
					continue;

				Map.Entry<String, JsonElement> statusEntry = iterator.next();

				String serviceAddress = statusEntry.getKey();
				if (!services.containsKey(serviceAddress))
					continue;

				services.get(serviceAddress).setStatus(Status.fromMojang(statusEntry.getValue().getAsString()));
			}
		}

		lastPing = System.currentTimeMillis();
	}

	/**
	 * @return The retrieved raw JSON status returned by Mojang.
	 */
	private JsonArray getCheckRawContent()
	{
		try
		{
			final URLConnection connection = (new URL(CHECK_SERVICE_URI)).openConnection();
			connection.setUseCaches(false);

			return new JsonParser().parse(new BufferedReader(new InputStreamReader(connection.getInputStream()))).getAsJsonArray();
		}
		catch (IOException e)
		{
			PluginLogger.warning("An error occurred while contacting Mojang status service at " + CHECK_SERVICE_URI, e);
			return null;
		}
	}
}
