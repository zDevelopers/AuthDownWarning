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
package eu.carrade.amaury.AuthDownWarning.commands;

import eu.carrade.amaury.AuthDownWarning.AuthDownWarning;
import eu.carrade.amaury.AuthDownWarning.Permissions;
import eu.carrade.amaury.AuthDownWarning.status.Service;
import eu.carrade.amaury.AuthDownWarning.status.Status;
import eu.carrade.amaury.AuthDownWarning.utils.DateUtils;
import fr.zcraft.zlib.components.i18n.I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;


public class MojangStatusCommand implements CommandExecutor, TabCompleter
{
	private final static String STATUS_PREFIX = "\u2502 ";
	private final static String STATUS_SEPARATOR = ChatColor.DARK_GRAY + " - " + ChatColor.RESET;


	@Override
	public boolean onCommand(final CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 0 || !args[0].equalsIgnoreCase("update"))
		{
			if (!Permissions.STATUS_VIEW.isGrantedTo(sender))
			{
				sender.sendMessage(I.t("{ce}You are not allowed to view Mojang services status."));
				sender.sendMessage(I.t("{gray}You can visit{white} https://help.mojang.com {gray}to see them."));
				return true;
			}


			final Set<Service> services = AuthDownWarning.get().getStatus().getServices();


			// Global status  ----
			// If all services are fine: Running.
			// If some services are unstable, or some non-critical services are down: Unstable.
			// If some critical services are down: Down.

			Status globalStatus = Status.RUNNING;
			for (Service service : services)
			{
				if (service.getStatus() == Status.DOWN && service.isCritical())
				{
					globalStatus = Status.DOWN;
					break;
				}
				else if (service.getStatus() == Status.UNSTABLE || service.getStatus() == Status.DOWN)
				{
					globalStatus = Status.UNSTABLE;
				}
			}

			// Display

			if (sender instanceof Player) sender.sendMessage("");
			sender.sendMessage(I.t("{gold}{bold}Mojang services status") + STATUS_SEPARATOR + globalStatus.getColor() + globalStatus.getLocalizedTag());

			for (Service service : services)
			{
				sender.sendMessage(generateStatusLine(service));
			}

			sender.sendMessage(I.t("{gray}Last update: {0}.", DateUtils.getRelativeTime(AuthDownWarning.get().getStatus().getLastPingDate())));
			if (sender instanceof Player) sender.sendMessage("");

			return true;
		}

		else
		{
			if (!Permissions.STATUS_UPDATE.isGrantedTo(sender))
			{
				sender.sendMessage(I.t("{ce}You are not allowed to update Mojang services status."));
				return true;
			}

			sender.sendMessage(I.t("{cst}Updating Mojang services status..."));

			Bukkit.getScheduler().runTaskAsynchronously(AuthDownWarning.get(), new Runnable() {
				@Override
				public void run()
				{
					AuthDownWarning.get().getStatus().updateStatus();
					sender.sendMessage(I.t("{cst}Updated."));
				}
			});

			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 1 && "update".startsWith(args[0].toLowerCase()) && Permissions.STATUS_UPDATE.isGrantedTo(sender))
			return Collections.singletonList("update");

		return null;
	}


	private String generateStatusLine(Service service)
	{
		String line = service.getStatus().getColor() + STATUS_PREFIX + ChatColor.WHITE + service.getName();

		if (service.getStatus() != Status.RUNNING)
		{
			line += STATUS_SEPARATOR + service.getStatus().getColor();

			switch (service.getStatus())
			{
				case UNSTABLE:
					line += I.t("Service unstable");
					break;
				case DOWN:
					line += I.t("Service down");
					break;
				case UNKNOWN:
					line += I.t("Service status unknown");
					break;
			}
		}

		return line;
	}
}
