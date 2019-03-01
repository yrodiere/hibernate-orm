/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type.descriptor.java;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.type.descriptor.java.LocalDateTimeJavaDescriptor;

import org.hibernate.testing.junit4.CustomParameterized;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hibernate.testing.junit4.ExtraAssertions.assertCalendarApproximatelyEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Jordan Gigov
 * @author SH
 * @author Yoann Rodiere
 */
@RunWith( CustomParameterized.class )
public class LocalDateTimeDescriptorTest extends AbstractDescriptorTest<LocalDateTime> {

	/*
	 * The default timezone affects conversions done using java.util,
	 * which is why we take it into account even when testing LocalDateTime.
	 */
	@Parameterized.Parameters(name = "{0}-{1}-{2}T{3}:{4}:{5} [JVM TZ: {6}]")
	public static List<Object[]> data() {
		return Arrays.asList(
				// Not affected by HHH-13266
				data( 2017, 11, 6, 19, 19, 1, ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, ZoneId.of( "Europe/Paris" ) ),
				data( 1970, 1, 1, 0, 0, 0, ZoneId.of( "GMT" ) ),
				data( 1900, 1, 1, 0, 0, 0, ZoneId.of( "GMT" ) ),
				data( 1900, 1, 1, 0, 0, 0, ZoneId.of( "Europe/Oslo" ) ),
				data( 1900, 1, 2, 0, 9, 21, ZoneId.of( "Europe/Paris" ) ),
				data( 1900, 1, 2, 0, 19, 32, ZoneId.of( "Europe/Amsterdam" ) ),
				// Affected by HHH-13266
				data( 1892, 1, 1, 0, 0, 0, ZoneId.of( "Europe/Oslo" ) ),
				data( 1900, 1, 1, 0, 9, 20, ZoneId.of( "Europe/Paris" ) ),
				data( 1900, 1, 1, 0, 19, 31, ZoneId.of( "Europe/Amsterdam" ) ),
				data( 1600, 1, 1, 0, 0, 0, ZoneId.of( "Europe/Amsterdam" ) )
		);
	}

	private static Object[] data(int year, int month, int day,
			int hour, int minute, int second, ZoneId defaultTimeZone) {
		return new Object[] { year, month, day, hour, minute, second, defaultTimeZone };
	}

	private final TimeZone previousDefaultTimeZone = TimeZone.getDefault();

	private final int year;
	private final int month;
	private final int day;
	private final int hour;
	private final int minute;
	private final int second;
	private final ZoneId defaultTimeZone;

	public LocalDateTimeDescriptorTest(int year, int month, int day,
			int hour, int minute, int second, ZoneId defaultTimeZone) {
		super( LocalDateTimeJavaDescriptor.INSTANCE);
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.defaultTimeZone = defaultTimeZone;
	}

	@Before
	public void setCustomDefaultTimeZone() {
		TimeZone.setDefault( TimeZone.getTimeZone( defaultTimeZone ) );
	}

	@After
	public void restoreDefaultTimeZone() {
		TimeZone.setDefault( previousDefaultTimeZone );
	}

	@Override
	protected Data<LocalDateTime> getTestData() {
		return new Data<>( getLocalDateTime(), getLocalDateTime(), getLocalDateTime().minusMinutes( 2 ) );
	}

	@Override
	protected boolean shouldBeMutable() {
		return false;
	}

	@Test
	public void wrap_null() {
		assertNull( getTypeDescriptor().wrap( null, null ) );
	}

	@Test
	public void wrap_localDateTime() {
		assertEquals( getLocalDateTime(), getTypeDescriptor().wrap( getLocalDateTime(), null ) );
	}

	@Test
	public void wrap_date() {
		assertEquals( getLocalDateTime(), getTypeDescriptor().wrap( getDate(), null ) );
	}

	@Test
	public void wrap_sqlDate() {
		assertEquals( getLocalDateTime(), getTypeDescriptor().wrap( getSqlDate(), null ) );
	}

	@Test
	public void wrap_timestamp() {
		assertEquals( getLocalDateTime(), getTypeDescriptor().wrap( getTimestamp(), null ) );
	}

	@Test
	public void wrap_calendar() {
		assertEquals( getLocalDateTime(), getTypeDescriptor().wrap( getCalendar(), null ) );
	}

	@Test
	public void wrap_long() {
		assertEquals( getLocalDateTime(), getTypeDescriptor().wrap( getDateAsLong(), null ) );
	}

	@Test
	public void unwrap_null() {
		assertNull( getTypeDescriptor().unwrap( null, Date.class, null ) );
	}

	@Test
	public void unwrap_localDateTime() {
		assertEquals( getLocalDateTime(), getTypeDescriptor().unwrap( getLocalDateTime(), LocalDateTime.class, null ) );
	}

	@Test
	public void unwrap_date() {
		assertEquals( getDate(), getTypeDescriptor().unwrap( getLocalDateTime(), Date.class, null ) );
	}

	@Test
	public void unwrap_sqlDate() {
		assertEquals( getSqlDate(), getTypeDescriptor().unwrap( getLocalDateTime(), java.sql.Date.class, null ) );
	}

	@Test
	public void unwrap_timestamp() {
		assertEquals(
				getTimestamp(),
				getTypeDescriptor().unwrap( getLocalDateTime(), Timestamp.class, null )
		);
	}

	@Test
	public void unwrap_calendar() {
		// GregorianCalendar.equals is notoriously broken, so we have to compare the fields we're interested in directly
		assertCalendarApproximatelyEquals( getCalendar(), getTypeDescriptor().unwrap( getLocalDateTime(), Calendar.class, null ) );
	}

	@Test
	public void unwrap_long() {
		assertEquals(
				getDateAsLong(),
				getTypeDescriptor().unwrap( getLocalDateTime(), Long.class, null )
		);
	}

	private LocalDateTime getLocalDateTime() {
		return LocalDateTime.of( year, month, day, hour, minute, second );
	}

	private Date getDate() {
		return new Date( year - 1900, month - 1, day, hour, minute, second );
	}

	private java.sql.Date getSqlDate() {
		return new java.sql.Date( year - 1900, month - 1, day );
	}

	private Timestamp getTimestamp() {
		return new Timestamp(
				year - 1900, month - 1, day,
				hour, minute, second, 0
		);
	}

	private Long getDateAsLong() {
		return getLocalDateTime().atZone( ZoneId.systemDefault() ).toInstant().toEpochMilli();
	}

	private Calendar getCalendar() {
		return new GregorianCalendar( year, month - 1, day, hour, minute, second );
	}
}
