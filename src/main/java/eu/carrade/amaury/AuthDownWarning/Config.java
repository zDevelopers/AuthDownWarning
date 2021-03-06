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
package eu.carrade.amaury.AuthDownWarning;

import fr.zcraft.zlib.components.configuration.Configuration;
import fr.zcraft.zlib.components.configuration.ConfigurationItem;

import java.util.Locale;

import static fr.zcraft.zlib.components.configuration.ConfigurationItem.item;


public final class Config extends Configuration
{
	public static ConfigurationItem<Locale> LOCALE = item("locale", Locale.class);

	public static ConfigurationItem<Integer> REFRESH_INTERVAL = item("check_interval", 90);
	public static ConfigurationItem<Integer> WARNING_INTERVAL = item("warning_interval", 600);

	public static ConfigurationItem<Boolean> DISPLAY_SERVICES_DOWN = item("display_services_down", false);

	public static ConfigurationItem<String> CHECK_SERVICE_URI = item("check_uri", "https://status.mojang.com/check");
}
