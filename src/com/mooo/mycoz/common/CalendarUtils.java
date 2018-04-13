/**
 * $RCSfile: StringUtils.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2007/03/01 00:17:45 $
 *
 * Copyright (C) 2000 CoolServlets.com. All rights reserved.
 *
 * ===================================================================
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        CoolServlets.com (http://www.Yasna.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Jive" and "CoolServlets.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact webmaster@Yasna.com.
 *
 * 5. Products derived from this software may not be called "Jive",
 *    nor may "Jive" appear in their name, without prior written
 *    permission of CoolServlets.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL COOLSERVLETS.COM OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of CoolServlets.com. For more information
 * on CoolServlets.com, please see <http://www.Yasna.com>.
 */
package com.mooo.mycoz.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class to peform common String manipulation algorithms.
 */
public class CalendarUtils {

	public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";

	public static final String YMDHM = "yyyy-MM-dd HH:mm";

	public static final String NYMDHM = "yyyyMMddHHmm";

	public static final String YMDH = "yyyy-MM-dd HH";

	public static final String NYMDH = "yyyyMMddHH";

	public static final String YMD = "yyyy-MM-dd";

	public static final String NYMD = "yyyyMMdd";

	public static final String HHMM = "HH:mm";

	public static final String YEAR = "yyyy";

	public static String dtformat(Date date,String sformat){
		return new SimpleDateFormat(sformat).format(date);
	}

	public static Date dtparse(String dateValue,String sformat) throws ParseException{
		return new SimpleDateFormat(sformat).parse(dateValue);
	}

	//日期偏移量
	public static String dtoffset(String dtbegin,String dtformat,
				int yoffset,int moffset,int doffset,int hoffset) {
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(dtparse(dtbegin,dtformat));

			now.add(Calendar.YEAR, yoffset);
			now.add(Calendar.MONTH, moffset);
			now.add(Calendar.DAY_OF_MONTH, doffset);
			now.add(Calendar.HOUR_OF_DAY, hoffset);

			return dtformat(now.getTime(),dtformat);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Date getDate() {

		TimeZone tz = TimeZone.getDefault();
		Calendar now = Calendar.getInstance(tz);
		Date date = now.getTime();

		return date;
	}

	public static String getNowTime() {
		return dtformat(getDate(),HHMM);
	}

	public static String getNowYear() {
		return dtformat(getDate(),YEAR);
	}

	public static String getNow() {
		return dtformat(getDate(),YMDHM);
	}

	public static String getIdPrifix() {
		TimeZone tz = TimeZone.getDefault();
		Calendar now = Calendar.getInstance(tz);
		String prefix;
		int tmp = now.get(Calendar.YEAR);
		prefix = (tmp + "").substring(2, 4);
		tmp = now.get(Calendar.MONTH) + 1;
		if (tmp < 10)
			prefix += "0" + tmp;
		else
			prefix += tmp;

		return prefix;
	}

	public static String getYYMM() {
		return getIdPrifix();
	}

	public static String getYY() {
		TimeZone tz = TimeZone.getDefault();
		Calendar now = Calendar.getInstance(tz);
		String prefix;
		int tmp = now.get(Calendar.YEAR);
		prefix = (tmp + "").substring(2, 4);

		return prefix;
	}

	public static String getLastMonthToday() {
		// get default date in yyyy-mm-dd format
		TimeZone tz = TimeZone.getDefault();
		// tz.setID("Asia/Jakarta");
		Calendar now = Calendar.getInstance(tz);
		String prefix;
		int yy = now.get(Calendar.YEAR);
		int tmp = now.get(Calendar.MONTH);
		if (tmp == 0) {
			prefix = (yy - 1) + "";
			tmp = 12;
		} else {
			prefix = yy + "";
		}
		if (tmp < 10)
			prefix += "-0" + tmp;
		else
			prefix += "-" + tmp;

		tmp = now.get(Calendar.DATE);
		if (tmp < 10)
			prefix += "-0" + tmp;
		else
			prefix += "-" + tmp;

		return prefix;
	} /* getLastMonthToday() */

	public static String getToday() {
		// get default date in yyyy-mm-dd format
		TimeZone tz = TimeZone.getDefault();
		Calendar now = Calendar.getInstance(tz);

		String prefix;
		int tmp = now.get(Calendar.YEAR);
		prefix = tmp + "";
		tmp = now.get(Calendar.MONTH) + 1;
		if (tmp < 10)
			prefix += "-0" + tmp;
		else
			prefix += "-" + tmp;

		tmp = now.get(Calendar.DATE);
		if (tmp < 10)
			prefix += "-0" + tmp;
		else
			prefix += "-" + tmp;

		return prefix;
	} /* getToday() */

	//	private static List<?> search(Class<?> clazz) {
//		List<?> searchList = null;
//		try {
//			Object obj = clazz.newInstance();
//			Method execMethod = clazz.getMethod("searchAndRetrieveList");
//			searchList = (List<?>) execMethod.invoke(obj);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return searchList;
//	}
//
//	public static Object randomNo(Class<?> clazz) {
//		List<?> searchList = search(clazz);
//		Random random = new Random();
//		int randomId = searchList.size();
//		if (randomId > 0) {
//			random.nextInt(randomId);
//			return searchList.get(randomId);
//		} else {
//			randomId = 0;
//		}
//		return 0;
//	}

	public static int randomInt(int max) {
		if (max > 0)
			return new Random().nextInt(max);

		return 0;
	}

	public static Object randomNo(List<?> retrieveList) {
		Random random = new Random();
		int randomId = retrieveList.size();
		if (randomId > 0) {
			random.nextInt(randomId);
			return retrieveList.get(randomId);
		} else {
			randomId = 0;
		}
		return 0;
	}

	public static Date randomDate() {
		Date randomDate = new Date();
		Random random = new Random();

		Calendar cal = Calendar.getInstance();
		cal.set(2010, 8, 1);
		long start = cal.getTimeInMillis();
		cal.set(2010, 9, 13);
		long end = cal.getTimeInMillis();
		for (int i = 0; i < 10; i++) {
			randomDate = new Date(start
					+ (long) (random.nextDouble() * (end - start)));
		}

		return randomDate;
	}
}