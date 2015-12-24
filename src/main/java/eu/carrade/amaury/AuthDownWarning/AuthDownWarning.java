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

import eu.carrade.amaury.AuthDownWarning.commands.MojangStatusCommand;
import eu.carrade.amaury.AuthDownWarning.status.MojangStatus;
import eu.carrade.amaury.AuthDownWarning.status.critical.CriticalStatusChangeChecker;
import eu.carrade.amaury.AuthDownWarning.status.critical.WarningsSender;
import eu.carrade.amaury.AuthDownWarning.tasks.MojangStatusCheckTask;
import fr.zcraft.zlib.components.configuration.Configuration;
import fr.zcraft.zlib.core.ZPlugin;
import fr.zcraft.zlib.tools.runners.RunAsyncTask;
import org.bukkit.command.PluginCommand;


public class AuthDownWarning extends ZPlugin
{
	private static AuthDownWarning instance;

	private MojangStatus status;
	private CriticalStatusChangeChecker criticalStatusChangeChecker;

	@Override
	public void onEnable()
	{
		instance = this;


		// Config
		saveDefaultConfig();
		Configuration.init(Config.class);


		// Services
		status = new MojangStatus();
		criticalStatusChangeChecker = new CriticalStatusChangeChecker();


		// Commands
		final MojangStatusCommand statusExecutor = new MojangStatusCommand();
		final PluginCommand       statusCommand  = getCommand("mojangstatus");

		statusCommand.setExecutor(statusExecutor);
		statusCommand.setTabCompleter(statusExecutor);


		// Listeners
		getServer().getPluginManager().registerEvents(new WarningsSender(), this);


		// Tasks
		RunAsyncTask.timer(new MojangStatusCheckTask(), 1l, Config.REFRESH_INTERVAL.get() * 20l);
	}


	public MojangStatus getStatus()
	{
		return status;
	}

	public CriticalStatusChangeChecker getCriticalStatusChangeChecker()
	{
		return criticalStatusChangeChecker;
	}


	public static AuthDownWarning get()
	{
		return instance;
	}
}
