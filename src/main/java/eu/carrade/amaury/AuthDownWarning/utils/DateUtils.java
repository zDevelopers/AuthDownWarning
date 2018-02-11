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
package eu.carrade.amaury.AuthDownWarning.utils;

import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zlib.components.i18n.I18n;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public final class DateUtils
{
	public static String getRelativeTime(Date date)
	{
		Long secondsDiff = (System.currentTimeMillis() - date.getTime()) / 1000;

		if (secondsDiff < 60)
		{
			return I.tn("{0} second ago", "{0} seconds ago", secondsDiff.intValue());
		}
		else if (secondsDiff < 3600)
		{
			final int minutes = (int) Math.rint(secondsDiff / 60d);
			return I.tn("{0} minute ago", "{0} minutes ago", minutes);
		}
		else if (secondsDiff < 86400)
		{
			final int hours = (int) Math.rint(secondsDiff / 3600d);
			return I.tn("{0} hour ago", "{0} hours ago", hours);
		}
		else
		{
			try
			{
				DateFormat formatter;
				try { formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, I18n.getPrimaryLocale()); }
				catch (Exception e) { formatter = DateFormat.getDateTimeInstance(); }

				return formatter.format(date);
			}
			catch (Exception e)
			{
				Calendar cal = Calendar.getInstance(I18n.getPrimaryLocale());
				cal.setTime(date);

				return I.t("{0}/{1}/{2} at {3}:{4}", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
			}
		}
	}
}
