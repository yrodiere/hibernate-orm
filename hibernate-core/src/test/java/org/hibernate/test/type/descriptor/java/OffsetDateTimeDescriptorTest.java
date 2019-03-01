/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type.descriptor.java;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.type.descriptor.java.OffsetDateTimeJavaDescriptor;

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
public class OffsetDateTimeDescriptorTest extends AbstractDescriptorTest<OffsetDateTime> {

	/*
	 * The default timezone affects conversions done using java.util,
	 * which is why we take it into account on top of the offset.
	 */
	@Parameterized.Parameters(name = "{0}-{1}-{2}T{3}:{4}:{5}[{6}] [JVM TZ: {7}]")
	public static List<Object[]> data() {
		return Arrays.asList(
				// Not affected by HHH-13266
				data( 2017, 11, 6, 19, 19, 1, "+10:00", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+07:00", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+01:30", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+01:00", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+00:30", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "-02:00", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "-06:00", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "-08:00", ZoneId.of( "UTC-8" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+10:00", ZoneId.of( "Europe/Paris" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+07:00", ZoneId.of( "Europe/Paris" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+01:30", ZoneId.of( "Europe/Paris" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+01:00", ZoneId.of( "Europe/Paris" ) ),
				data( 2017, 11, 6, 19, 19, 1, "+00:30", ZoneId.of( "Europe/Paris" ) ),
				data( 2017, 11, 6, 19, 19, 1, "-02:00", ZoneId.of( "Europe/Paris" ) ),
				data( 2017, 11, 6, 19, 19, 1, "-06:00", ZoneId.of( "Europe/Paris" ) ),
				data( 2017, 11, 6, 19, 19, 1, "-08:00", ZoneId.of( "Europe/Paris" ) ),
				data( 1970, 1, 1, 0, 0, 0, "+01:00", ZoneId.of( "GMT" ) ),
				data( 1970, 1, 1, 0, 0, 0, "+00:00", ZoneId.of( "GMT" ) ),
				data( 1970, 1, 1, 0, 0, 0, "-01:00", ZoneId.of( "GMT" ) ),
				data( 1900, 1, 1, 0, 0, 0, "+01:00", ZoneId.of( "GMT" ) ),
				data( 1900, 1, 1, 0, 0, 0, "+00:00", ZoneId.of( "GMT" ) ),
				data( 1900, 1, 1, 0, 0, 0, "-01:00", ZoneId.of( "GMT" ) ),
				data( 1900, 1, 1, 0, 0, 0, "+00:00", ZoneId.of( "Europe/Oslo" ) ),
				data( 1900, 1, 2, 0, 9, 21, "+00:09:21", ZoneId.of( "Europe/Paris" ) ),
				data( 1900, 1, 2, 0, 19, 32, "+00:19:32", ZoneId.of( "Europe/Paris" ) ),
				data( 1900, 1, 2, 0, 19, 32, "+00:19:32", ZoneId.of( "Europe/Amsterdam" ) ),
				// Affected by HHH-13266
				data( 1892, 1, 1, 0, 0, 0, "+00:00", ZoneId.of( "Europe/Oslo" ) ),
				data( 1900, 1, 1, 0, 9, 20, "+00:09:21", ZoneId.of( "Europe/Paris" ) ),
				data( 1900, 1, 1, 0, 19, 31, "+00:19:32", ZoneId.of( "Europe/Paris" ) ),
				data( 1900, 1, 1, 0, 19, 31, "+00:19:32", ZoneId.of( "Europe/Amsterdam" ) ),
				data( 1600, 1, 1, 0, 0, 0, "+00:19:32", ZoneId.of( "Europe/Amsterdam" ) )
		);
	}

	private static Object[] data(int year, int month, int day,
			int hour, int minute, int second, String offset, ZoneId defaultTimeZone) {
		return new Object[] { year, month, day, hour, minute, second, offset, defaultTimeZone };
	}

	private final TimeZone previousDefaultTimeZone = TimeZone.getDefault();

	private final int year;
	private final int month;
	private final int day;
	private final int hour;
	private final int minute;
	private final int second;
	private final String offset;
	private final ZoneId defaultTimeZone;

	public OffsetDateTimeDescriptorTest(int year, int month, int day,
			int hour, int minute, int second, String offset, ZoneId defaultTimeZone) {
		super( OffsetDateTimeJavaDescriptor.INSTANCE);
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.offset = offset;
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
	protected Data<OffsetDateTime> getTestData() {
		return new Data<>( getOffsetDateTime(), getOffsetDateTime(), getOffsetDateTime().minusMinutes( 2 ) );
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
	public void wrap_offsetDateTime() {
		assertEquals( getOffsetDateTime(), getTypeDescriptor().wrap( getOffsetDateTime(), null ) );
	}

	@Test
	public void wrap_date() {
		assertEquals( getOffsetDateTime(), getTypeDescriptor().wrap( getDate(), null ) );
	}

	@Test
	public void wrap_sqlDate() {
		assertEquals( getOffsetDateTime(), getTypeDescriptor().wrap( getSqlDate(), null ) );
	}

	@Test
	public void wrap_timestamp() {
		assertEquals( getOffsetDateTime(), getTypeDescriptor().wrap( getTimestamp(), null ) );
	}

	@Test
	public void wrap_calendar() {
		assertEquals( getOffsetDateTime(), getTypeDescriptor().wrap( getCalendar(), null ) );
	}

	@Test
	public void wrap_long() {
		assertEquals( getOffsetDateTime(), getTypeDescriptor().wrap( getDateAsLong(), null ) );
	}

	@Test
	public void unwrap_null() {
		assertNull( getTypeDescriptor().unwrap( null, Date.class, null ) );
	}

	@Test
	public void unwrap_offsetDateTime() {
		assertEquals( getOffsetDateTime(), getTypeDescriptor().unwrap( getOffsetDateTime(), OffsetDateTime.class, null ) );
	}

	@Test
	public void unwrap_date() {
		assertEquals( getDate(), getTypeDescriptor().unwrap( getOffsetDateTime(), Date.class, null ) );
	}

	@Test
	public void unwrap_sqlDate() {
		assertEquals( getSqlDate(), getTypeDescriptor().unwrap( getOffsetDateTime(), java.sql.Date.class, null ) );
	}

	@Test
	public void unwrap_timestamp() {
		assertEquals(
				getTimestamp(),
				getTypeDescriptor().unwrap( getOffsetDateTime(), Timestamp.class, null )
		);
	}

	@Test
	public void unwrap_calendar() {
		// GregorianCalendar.equals is notoriously broken, so we have to compare the fields we're interested in directly
		assertCalendarApproximatelyEquals( getCalendar(), getTypeDescriptor().unwrap( getOffsetDateTime(), Calendar.class, null ) );
	}

	@Test
	public void unwrap_long() {
		assertEquals(
				getDateAsLong(),
				getTypeDescriptor().unwrap( getOffsetDateTime(), Long.class, null )
		);
	}

	private OffsetDateTime getOffsetDateTime() {
		return OffsetDateTime.of( year, month, day, hour, minute, second, 0, ZoneOffset.of( offset ) );
	}

	private Date getDate() {
		return getCalendar().getTime();
	}

	private java.sql.Date getSqlDate() {
		// Expect a date representing
		Calendar calendar = getCalendar();
		for ( int field : new int[] { Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND } ) {
			calendar.clear( field );
		}
		return new java.sql.Date( calendar.getTimeInMillis() );
	}

	private Timestamp getTimestamp() {
		return new Timestamp( getCalendar().getTimeInMillis() );
	}

	private Long getDateAsLong() {
		return getOffsetDateTime().toInstant().toEpochMilli();
	}

	private Calendar getCalendar() {
		GregorianCalendar calendar = new GregorianCalendar( TimeZone.getTimeZone( "GMT" + offset ) );
		calendar.set( year, month - 1, day, hour, minute, second );
		return calendar;
	}
}
