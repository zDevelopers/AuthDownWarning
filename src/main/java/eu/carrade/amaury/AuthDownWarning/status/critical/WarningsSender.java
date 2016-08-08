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
package eu.carrade.amaury.AuthDownWarning.status.critical;

import eu.carrade.amaury.AuthDownWarning.AuthDownWarning;
import eu.carrade.amaury.AuthDownWarning.Config;
import eu.carrade.amaury.AuthDownWarning.Permissions;
import eu.carrade.amaury.AuthDownWarning.events.AsyncCriticalMojangStatusChangedEvent;
import eu.carrade.amaury.AuthDownWarning.status.Service;
import eu.carrade.amaury.AuthDownWarning.status.Status;
import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zlib.tools.runners.RunTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;


public class WarningsSender implements Listener
{
	private final Long MINIMAL_WARNING_INTERVAL;

	private BukkitTask warningTask = null;


	public WarningsSender()
	{
		MINIMAL_WARNING_INTERVAL = Config.WARNING_INTERVAL.get() * 20l;
	}


	@EventHandler
	public void onCriticalStatusChange(AsyncCriticalMojangStatusChangedEvent ev)
	{
		if (warningTask != null)
		{
			warningTask.cancel();
			warningTask = null;
		}


		if (ev.getNewStatus() == Status.RUNNING)
		{
			RunTask.nextTick(new Runnable() {
				@Override
				public void run()
				{
					broadcastWarning(I.t("{darkgreen}{bold}Mojang servers are back online."));
				}
			});
		}
		else
		{
			final Status criticalStatus = ev.getNewStatus();

			warningTask = RunTask.timer(new Runnable() {
				@Override
				public void run()
				{
					// We first retrieve the down services

					String servicesDownNames = "";
					Integer servicesDownCount = 0;

					if (Config.DISPLAY_SERVICES_DOWN.get())
					{
						for (Service criticalService : AuthDownWarning.get().getStatus().getCriticalServices())
						{
							if (criticalService.getStatus() == criticalStatus)
							{
								servicesDownNames += criticalService.getName() + ", ";
								servicesDownCount++;
							}
						}

						servicesDownNames = servicesDownNames.substring(0, servicesDownNames.length() - 2);  // Removes the last comma
					}


					// Then we warn about this

					String title = "";
					String servicesDownLine = "";

					switch (criticalStatus)
					{
						case UNSTABLE:
							title = I.t("Mojang servers are currently unstable.");
							servicesDownLine = I.tn("{gray}Unstable service: {0}", "{gray}Unstable services: {0}", servicesDownCount, servicesDownNames);
							break;

						case DOWN:
							title = I.t("Mojang servers are currently down.");
							servicesDownLine = I.tn("{gray}Down service: {0}", "{gray}Down services: {0}", servicesDownCount, servicesDownNames);
							break;
					}

					broadcastWarning(
							criticalStatus.getColor() + "" + ChatColor.BOLD + title,
							criticalStatus.getColor() + I.t("Don't log out: you might not be able to log in again."),
							Config.DISPLAY_SERVICES_DOWN.get() ? servicesDownLine : ""
					);
				}
			}, 1l, MINIMAL_WARNING_INTERVAL);
		}
	}

	private void broadcastWarning(String... messages)
	{
		Bukkit.broadcast("", Permissions.WARNINGS_RECEIVE.getPermission());

		for (String message : messages)
			if (!message.isEmpty())
				Bukkit.broadcast(message, Permissions.WARNINGS_RECEIVE.getPermission());

		Bukkit.broadcast("", Permissions.WARNINGS_RECEIVE.getPermission());
	}
}
