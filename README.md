# Authentication down → Warning [![Build Status](http://jenkins.carrade.eu/job/AuthDownWarning/badge/icon)](http://jenkins.carrade.eu/job/AuthDownWarning)

A small Bukkit plugin to warn players when Mojang servers are down.

### Warnings

When the authentification or sessions servers become down, a message like this one will be broadcasted.  
![Warning message if the authentification servers are down](http://raw.carrade.eu/s/1451071102.png)  
A similar message will be displayed if the servers are unstable.

This message will be broadcasted every ten minutes (by default) until the servers status change.  
![“Fine again” message](http://raw.carrade.eu/s/1450919965.png)  
 

### Manual check

A command is available to see the status of the Mojang services from in-game: `/mojangstatus`. It contains all the services displayed in the [Mojang status page](https://help.mojang.com).  
![`/mojangstatus` preview](http://raw.carrade.eu/s/1450920092.png)

The status can also be force-updated using `/mojangstatus update`. By default, they are updated in the background every 90 seconds.  
 

### Configuration

The main configuration entries are the following:
```yml
# The number of seconds between two checks against the Mojang servers.
check_interval: 90

# The minimal number of seconds between two warning sent to the players for
# the same kind of status.
# Players will always be immediately notified if the status changes.
warning_interval: 600


# Set to true to display a list of the down or unstable services in the
# warning message, like this: http://raw.carrade.eu/s/1450919953.png
display_services_down: false
```
Another is available to change the status check URI; see the [`config.yml`](https://github.com/zDevelopers/AuthDownWarning/blob/master/src/main/resources/config.yml) file.  
 

### Permissions

Permission | Default | Meaning
-----------|---------|--------
`authdownwarning.status.view`|Everyone|Allows a player to see the results of the last check using `/mojangstatus`.
`authdownwarning.status.update`|Operators|Allows a player to force-update the Mojang services status with `/mojangstatus update`.
`authdownwarning.warnings.receive`|Everyone|All players with this permission will receive the warnings if some of the Mojang services are down.
