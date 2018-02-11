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

import java.util.Date;


public class Service
{
	private String name;
	private String address;

	private Boolean critical;

	private Status status = Status.UNKNOWN;
	private Long lastUpdate = -1l;

	/**
	 * @param name The service friendly name.
	 * @param address The service address, as displayed in the Mojang JSON check.
	 * @param critical true to warn players if this service is down or unstable.
	 */
	public Service(String name, String address, Boolean critical)
	{
		this.name = name;
		this.address = address;
		this.critical = critical;
	}


	public String getName()
	{
		return name;
	}

	public String getAddress()
	{
		return address;
	}

	public Status getStatus()
	{
		return status;
	}

	public Date getLastUpdateDate()
	{
		return new Date(lastUpdate);
	}

	public Boolean isCritical()
	{
		return critical;
	}


	public void setStatus(Status status)
	{
		this.status = status;
		this.lastUpdate = System.currentTimeMillis();
	}

	@Override
	public String toString()
	{
		return "Service{" +
				"name='" + name + '\'' +
				", address='" + address + '\'' +
				", critical=" + critical +
				", status=" + status +
				'}';
	}
}
