/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.type.LocalDateTimeType;
import org.hibernate.type.descriptor.WrapperOptions;

/**
 * Java type descriptor for the LocalDateTime type.
 *
 * @author Steve Ebersole
 */
public class LocalDateTimeJavaDescriptor extends AbstractTypeDescriptor<LocalDateTime> {
	/**
	 * Singleton access
	 */
	public static final LocalDateTimeJavaDescriptor INSTANCE = new LocalDateTimeJavaDescriptor();

	@SuppressWarnings("unchecked")
	public LocalDateTimeJavaDescriptor() {
		super( LocalDateTime.class, ImmutableMutabilityPlan.INSTANCE );
	}

	@Override
	public String toString(LocalDateTime value) {
		return LocalDateTimeType.FORMATTER.format( value );
	}

	@Override
	public LocalDateTime fromString(String string) {
		return LocalDateTime.from( LocalDateTimeType.FORMATTER.parse( string ) );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <X> X unwrap(LocalDateTime value, Class<X> type, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}

		if ( LocalDateTime.class.isAssignableFrom( type ) ) {
			return (X) value;
		}

		if ( java.sql.Timestamp.class.isAssignableFrom( type ) ) {
			Instant instant = value.atZone( ZoneId.systemDefault() ).toInstant();
			return (X) java.sql.Timestamp.from( instant );
		}

		if ( java.sql.Date.class.isAssignableFrom( type ) ) {
			return (X) java.sql.Date.valueOf( value.toLocalDate() );
		}

		if ( java.sql.Time.class.isAssignableFrom( type ) ) {
			return (X) Time.valueOf( value.toLocalTime() );
		}

		if ( Long.class.isAssignableFrom( type ) ) {
			Instant instant = value.atZone( ZoneId.systemDefault() ).toInstant();
			return (X) Long.valueOf( instant.toEpochMilli() );
		}

		GregorianCalendar calendar = new GregorianCalendar(
				value.getYear() - 1900, value.getMonthValue() - 1, value.getDayOfMonth(),
				value.getHour(), value.getMinute(), value.getSecond()
		);
		calendar.set( Calendar.MILLISECOND, value.getNano() / 1_000_000 );

		if ( java.util.Date.class.isAssignableFrom( type ) ) {
			return (X) calendar.getTime();
		}

		if ( Calendar.class.isAssignableFrom( type ) ) {
			return (X) calendar;
		}

		throw unknownUnwrap( type );
	}

	@Override
	public <X> LocalDateTime wrap(X value, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}

		if ( LocalDateTime.class.isInstance( value ) ) {
			return (LocalDateTime) value;
		}

		if ( Timestamp.class.isInstance( value ) ) {
			final Timestamp ts = (Timestamp) value;
			return LocalDateTime.ofInstant( ts.toInstant(), ZoneId.systemDefault() );
		}

		if ( Long.class.isInstance( value ) ) {
			final Instant instant = Instant.ofEpochMilli( (Long) value );
			return LocalDateTime.ofInstant( instant, ZoneId.systemDefault() );
		}

		if ( Calendar.class.isInstance( value ) ) {
			final Calendar calendar = (Calendar) value;
			return LocalDateTime.ofInstant( calendar.toInstant(), calendar.getTimeZone().toZoneId() );
		}

		if ( Date.class.isInstance( value ) ) {
			final Date ts = (Date) value;
			final Instant instant = Instant.ofEpochMilli( ts.getTime() );
			return LocalDateTime.ofInstant( instant, ZoneId.systemDefault() );
		}

		throw unknownWrap( value.getClass() );
	}
}
