/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@programmer.net]<br>
 * <br>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0<br>
 * <br>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.datatree.dom.converters;

import static io.datatree.dom.converters.DataConverterRegistry.register;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.DeepCloner;

/**
 * Converters for MongoDB / BSON object types.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
final class BsonConverterSet extends AbstractConverterSet {

	// --- PRIVATE CONSTRUCTOR ---

	private BsonConverterSet() {
	}

	// --- INIT MONGODB / BSON CONVERTERS ---

	static {

		// --- VALUE TO STRING CONVERTERS ---

		register(String.class, BsonNull.class, (from) -> {
			return null;
		});
		register(String.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? "true" : "false";
		});
		register(String.class, BsonDateTime.class, (from) -> {
			return dateToString(new Date(from.getValue()));
		});
		register(String.class, BsonDouble.class, (from) -> {
			return Double.toString(from.getValue());
		});
		register(String.class, BsonInt32.class, (from) -> {
			return Integer.toString(from.getValue());
		});
		register(String.class, BsonInt64.class, (from) -> {
			return Long.toString(from.getValue());
		});
		register(String.class, BsonString.class, (from) -> {
			return from.getValue();
		});
		register(String.class, BsonTimestamp.class, (from) -> {
			return dateToString(new Date(from.getTime() * 1000L));
		});
		register(String.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(String.class, Binary.class, (from) -> {
			return BASE64.encode(from.getData());
		});
		register(String.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().toString();
		});
		register(String.class, Code.class, (from) -> {
			return from.getCode();
		});
		register(String.class, ObjectId.class, (from) -> {
			return from.toHexString();
		});
		register(String.class, Symbol.class, (from) -> {
			return from.getSymbol();
		});
		register(String.class, BsonRegularExpression.class, (from) -> {
			return from.getPattern();
		});

		// --- VALUE TO BYTE ARRAY CONVERTERS ---

		register(byte[].class, BsonNull.class, (from) -> {
			return null;
		});
		register(byte[].class, BsonBoolean.class, (from) -> {
			return from.getValue() ? new byte[] { 1 } : new byte[] { 0 };
		});
		register(byte[].class, BsonDateTime.class, (from) -> {
			return numberStringToBytes(Long.toString(from.getValue()));
		});
		register(byte[].class, BsonDouble.class, (from) -> {
			return numberStringToBytes(Double.toString(from.getValue()));
		});
		register(byte[].class, BsonInt32.class, (from) -> {
			return numberStringToBytes(Integer.toString(from.getValue()));
		});
		register(byte[].class, BsonInt64.class, (from) -> {
			return numberStringToBytes(Long.toString(from.getValue()));
		});
		register(byte[].class, BsonString.class, (from) -> {
			String txt = from.getValue();
			if (isBase64String(txt)) {
				return BASE64.decode(txt);
			}
			return txt.getBytes(StandardCharsets.UTF_8);
		});
		register(byte[].class, BsonTimestamp.class, (from) -> {
			return numberStringToBytes(Integer.toString(from.getTime()));
		});
		register(byte[].class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(byte[].class, Binary.class, (from) -> {
			return from.getData();
		});
		register(byte[].class, Decimal128.class, (from) -> {
			return numberStringToBytes(from.bigDecimalValue().toString());
		});
		register(byte[].class, Code.class, (from) -> {
			return from.getCode().getBytes(StandardCharsets.UTF_8);
		});
		register(byte[].class, ObjectId.class, (from) -> {
			return from.toByteArray();
		});
		register(byte[].class, Symbol.class, (from) -> {
			return from.getSymbol().getBytes(StandardCharsets.UTF_8);
		});
		register(byte[].class, BsonRegularExpression.class, (from) -> {
			return from.getPattern().getBytes(StandardCharsets.UTF_8);
		});

		// --- VALUE TO BYTE CONVERTERS ---

		register(Byte.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Byte.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? (byte) 1 : (byte) 0;
		});
		register(Byte.class, BsonDateTime.class, (from) -> {
			return (byte) from.getValue();
		});
		register(Byte.class, BsonDouble.class, (from) -> {
			return (byte) from.getValue();
		});
		register(Byte.class, BsonInt32.class, (from) -> {
			return (byte) from.getValue();
		});
		register(Byte.class, BsonInt64.class, (from) -> {
			return (byte) from.getValue();
		});
		register(Byte.class, BsonString.class, (from) -> {
			return (byte) Long.parseLong(toNumericString(from.getValue(), false));
		});
		register(Byte.class, BsonTimestamp.class, (from) -> {
			return (byte) from.getTime();
		});
		register(Byte.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Byte.class, Binary.class, (from) -> {
			byte[] array = from.getData();
			return array.length > 0 ? array[0] : 0;
		});
		register(Byte.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().byteValue();
		});
		register(Byte.class, Code.class, (from) -> {
			return (byte) from.getCode().hashCode();
		});
		register(Byte.class, ObjectId.class, (from) -> {
			return (byte) from.hashCode();
		});
		register(Byte.class, Symbol.class, (from) -> {
			return (byte) from.getSymbol().hashCode();
		});
		register(Byte.class, BsonRegularExpression.class, (from) -> {
			return (byte) from.getPattern().hashCode();
		});

		// --- VALUE TO SHORT CONVERTERS ---

		register(Short.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Short.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? (short) 1 : (short) 0;
		});
		register(Short.class, BsonDateTime.class, (from) -> {
			return (short) from.getValue();
		});
		register(Short.class, BsonDouble.class, (from) -> {
			return (short) from.getValue();
		});
		register(Short.class, BsonInt32.class, (from) -> {
			return (short) from.getValue();
		});
		register(Short.class, BsonInt64.class, (from) -> {
			return (short) from.getValue();
		});
		register(Short.class, BsonString.class, (from) -> {
			return Short.parseShort(toNumericString(from.getValue(), false));
		});
		register(Short.class, BsonTimestamp.class, (from) -> {
			return (short) from.getTime();
		});
		register(Short.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Short.class, Binary.class, (from) -> {
			return new BigInteger(from.getData()).shortValue();
		});
		register(Short.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().shortValue();
		});
		register(Short.class, Code.class, (from) -> {
			return (short) from.getCode().hashCode();
		});
		register(Short.class, ObjectId.class, (from) -> {
			return (short) from.hashCode();
		});
		register(Short.class, Symbol.class, (from) -> {
			return (short) from.getSymbol().hashCode();
		});
		register(Short.class, BsonRegularExpression.class, (from) -> {
			return (short) from.getPattern().hashCode();
		});

		// --- VALUE TO INTEGER CONVERTERS ---

		register(Integer.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Integer.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? 1 : 0;
		});
		register(Integer.class, BsonDateTime.class, (from) -> {
			return (int) from.getValue();
		});
		register(Integer.class, BsonDouble.class, (from) -> {
			return (int) from.getValue();
		});
		register(Integer.class, BsonInt32.class, (from) -> {
			return from.getValue();
		});
		register(Integer.class, BsonInt64.class, (from) -> {
			return (int) from.getValue();
		});
		register(Integer.class, BsonString.class, (from) -> {
			return Integer.parseInt(toNumericString(from.getValue(), false));
		});
		register(Integer.class, BsonTimestamp.class, (from) -> {
			return from.getTime();
		});
		register(Integer.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Integer.class, Binary.class, (from) -> {
			return new BigInteger(from.getData()).intValue();
		});
		register(Integer.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().intValue();
		});
		register(Integer.class, Code.class, (from) -> {
			return from.getCode().hashCode();
		});
		register(Integer.class, ObjectId.class, (from) -> {
			return from.hashCode();
		});
		register(Integer.class, Symbol.class, (from) -> {
			return from.getSymbol().hashCode();
		});
		register(Integer.class, BsonRegularExpression.class, (from) -> {
			return from.getPattern().hashCode();
		});

		// --- VALUE TO LONG CONVERTERS ---

		register(Long.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Long.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? 1l : 0l;
		});
		register(Long.class, BsonDateTime.class, (from) -> {
			return from.getValue();
		});
		register(Long.class, BsonDouble.class, (from) -> {
			return (long) from.getValue();
		});
		register(Long.class, BsonInt32.class, (from) -> {
			return (long) from.getValue();
		});
		register(Long.class, BsonInt64.class, (from) -> {
			return from.getValue();
		});
		register(Long.class, BsonString.class, (from) -> {
			return Long.parseLong(toNumericString(from.getValue(), false));
		});
		register(Long.class, BsonTimestamp.class, (from) -> {
			return (long) from.getTime();
		});
		register(Long.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Long.class, Binary.class, (from) -> {
			return new BigInteger(from.getData()).longValue();
		});
		register(Long.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().longValue();
		});
		register(Long.class, Code.class, (from) -> {
			return (long) from.getCode().hashCode();
		});
		register(Long.class, ObjectId.class, (from) -> {
			return (long) from.hashCode();
		});
		register(Long.class, Symbol.class, (from) -> {
			return (long) from.getSymbol().hashCode();
		});
		register(Long.class, BsonRegularExpression.class, (from) -> {
			return (long) from.getPattern().hashCode();
		});

		// --- VALUE TO FLOAT CONVERTERS ---

		register(Float.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Float.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? 1f : 0f;
		});
		register(Float.class, BsonDateTime.class, (from) -> {
			return (float) from.getValue();
		});
		register(Float.class, BsonDouble.class, (from) -> {
			return (float) from.getValue();
		});
		register(Float.class, BsonInt32.class, (from) -> {
			return (float) from.getValue();
		});
		register(Float.class, BsonInt64.class, (from) -> {
			return (float) from.getValue();
		});
		register(Float.class, BsonString.class, (from) -> {
			return Float.parseFloat(toNumericString(from.getValue(), true));
		});
		register(Float.class, BsonTimestamp.class, (from) -> {
			return (float) from.getTime();
		});
		register(Float.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Float.class, Binary.class, (from) -> {
			return new BigInteger(from.getData()).floatValue();
		});
		register(Float.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().floatValue();
		});
		register(Float.class, Code.class, (from) -> {
			return (float) from.getCode().hashCode();
		});
		register(Float.class, ObjectId.class, (from) -> {
			return (float) from.hashCode();
		});
		register(Float.class, Symbol.class, (from) -> {
			return (float) from.getSymbol().hashCode();
		});
		register(Float.class, BsonRegularExpression.class, (from) -> {
			return (float) from.getPattern().hashCode();
		});

		// --- VALUE TO DOUBLE CONVERTERS ---

		register(Double.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Double.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? 1d : 0d;
		});
		register(Double.class, BsonDateTime.class, (from) -> {
			return (double) from.getValue();
		});
		register(Double.class, BsonDouble.class, (from) -> {
			return from.doubleValue();
		});
		register(Double.class, BsonInt32.class, (from) -> {
			return (double) from.getValue();
		});
		register(Double.class, BsonInt64.class, (from) -> {
			return (double) from.getValue();
		});
		register(Double.class, BsonString.class, (from) -> {
			return Double.parseDouble(toNumericString(from.getValue(), true));
		});
		register(Double.class, BsonTimestamp.class, (from) -> {
			return (double) from.getTime();
		});
		register(Double.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Double.class, Binary.class, (from) -> {
			return new BigInteger(from.getData()).doubleValue();
		});
		register(Double.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().doubleValue();
		});
		register(Double.class, Code.class, (from) -> {
			return (double) from.getCode().hashCode();
		});
		register(Double.class, ObjectId.class, (from) -> {
			return (double) from.hashCode();
		});
		register(Double.class, Symbol.class, (from) -> {
			return (double) from.getSymbol().hashCode();
		});
		register(Double.class, BsonRegularExpression.class, (from) -> {
			return (double) from.getPattern().hashCode();
		});

		// --- VALUE TO BIGINTEGER CONVERTERS ---

		register(BigInteger.class, BsonNull.class, (from) -> {
			return null;
		});
		register(BigInteger.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? BigInteger.ONE : BigInteger.ZERO;
		});
		register(BigInteger.class, BsonDateTime.class, (from) -> {
			return new BigInteger(Long.toString(from.getValue()));
		});
		register(BigInteger.class, BsonDouble.class, (from) -> {
			return new BigInteger(toNumericString(Double.toString(from.getValue()), false));
		});
		register(BigInteger.class, BsonInt32.class, (from) -> {
			return new BigInteger(Integer.toString(from.getValue()));
		});
		register(BigInteger.class, BsonInt64.class, (from) -> {
			return new BigInteger(Long.toString(from.getValue()));
		});
		register(BigInteger.class, BsonString.class, (from) -> {
			return new BigInteger(toNumericString(from.getValue(), false));
		});
		register(BigInteger.class, BsonTimestamp.class, (from) -> {
			return new BigInteger(Integer.toString(from.getTime()));
		});
		register(BigInteger.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(BigInteger.class, Binary.class, (from) -> {
			return new BigInteger(from.getData());
		});
		register(BigInteger.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue().toBigInteger();
		});
		register(BigInteger.class, Code.class, (from) -> {
			return new BigInteger(Integer.toString(from.getCode().hashCode()));
		});
		register(BigInteger.class, ObjectId.class, (from) -> {
			return new BigInteger(Integer.toString(from.hashCode()));
		});
		register(BigInteger.class, Symbol.class, (from) -> {
			return new BigInteger(Integer.toString(from.getSymbol().hashCode()));
		});
		register(BigInteger.class, BsonRegularExpression.class, (from) -> {
			return new BigInteger(Integer.toString(from.getPattern().hashCode()));
		});

		// --- VALUE TO BIGDECIMAL CONVERTERS ---

		register(BigDecimal.class, BsonNull.class, (from) -> {
			return null;
		});
		register(BigDecimal.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? BigDecimal.ONE : BigDecimal.ZERO;
		});
		register(BigDecimal.class, BsonDateTime.class, (from) -> {
			return new BigDecimal(from.getValue());
		});
		register(BigDecimal.class, BsonDouble.class, (from) -> {
			return new BigDecimal(from.getValue());
		});
		register(BigDecimal.class, BsonInt32.class, (from) -> {
			return new BigDecimal(from.getValue());
		});
		register(BigDecimal.class, BsonInt64.class, (from) -> {
			return new BigDecimal(from.getValue());
		});
		register(BigDecimal.class, BsonString.class, (from) -> {
			return new BigDecimal(toNumericString(from.getValue(), true));
		});
		register(BigDecimal.class, BsonTimestamp.class, (from) -> {
			return new BigDecimal(from.getTime());
		});
		register(BigDecimal.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(BigDecimal.class, Binary.class, (from) -> {
			return new BigDecimal(new BigInteger(from.getData()));
		});
		register(BigDecimal.class, Decimal128.class, (from) -> {
			return from.bigDecimalValue();
		});
		register(BigDecimal.class, Code.class, (from) -> {
			return new BigDecimal(from.getCode().hashCode());
		});
		register(BigDecimal.class, ObjectId.class, (from) -> {
			return new BigDecimal(from.hashCode());
		});
		register(BigDecimal.class, Symbol.class, (from) -> {
			return new BigDecimal(from.getSymbol().hashCode());
		});
		register(BigDecimal.class, BsonRegularExpression.class, (from) -> {
			return new BigDecimal(from.getPattern().hashCode());
		});

		// --- VALUE TO BOOLEAN CONVERTERS ---

		register(Boolean.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Boolean.class, BsonBoolean.class, (from) -> {
			return from.getValue();
		});
		register(Boolean.class, BsonDateTime.class, (from) -> {
			return numberStringToBoolean(Long.toString(from.getValue()));
		});
		register(Boolean.class, BsonDouble.class, (from) -> {
			return numberStringToBoolean(Double.toString(from.getValue()));
		});
		register(Boolean.class, BsonInt32.class, (from) -> {
			return numberStringToBoolean(Integer.toString(from.getValue()));
		});
		register(Boolean.class, BsonInt64.class, (from) -> {
			return numberStringToBoolean(Long.toString(from.getValue()));
		});
		register(Boolean.class, BsonString.class, (from) -> {
			String txt = from.getValue().toLowerCase();
			if ("true".equals(txt) || "yes".equals(txt) || "on".equals(txt)) {
				return true;
			}
			if ("false".equals(txt) || "no".equals(txt) || "off".equals(txt)) {
				return false;
			}
			txt = toNumericString(String.valueOf(txt), true);
			return !(txt.isEmpty() || txt.equals("0") || txt.contains("-"));
		});
		register(Boolean.class, BsonTimestamp.class, (from) -> {
			return numberStringToBoolean(Integer.toString(from.getTime()));
		});
		register(Boolean.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Boolean.class, Binary.class, (from) -> {
			for (byte b : from.getData()) {
				if (b > 0) {
					return true;
				}
			}
			return false;
		});
		register(Boolean.class, Decimal128.class, (from) -> {
			return numberStringToBoolean(from.bigDecimalValue().toString());
		});
		register(Boolean.class, Code.class, (from) -> {
			return from.getCode().hashCode() > 0;
		});
		register(Boolean.class, ObjectId.class, (from) -> {
			return from.hashCode() > 0;
		});
		register(Boolean.class, Symbol.class, (from) -> {
			return from.getSymbol().hashCode() > 0;
		});
		register(Boolean.class, BsonRegularExpression.class, (from) -> {
			return from.getPattern().hashCode() > 0;
		});

		// --- VALUE TO DATE CONVERTERS ---

		register(Date.class, BsonNull.class, (from) -> {
			return null;
		});
		register(Date.class, BsonBoolean.class, (from) -> {
			return from.getValue() ? new Date(1) : new Date(0);
		});
		register(Date.class, BsonDateTime.class, (from) -> {
			return new Date(from.getValue());
		});
		register(Date.class, BsonDouble.class, (from) -> {
			return new Date(from.longValue());
		});
		register(Date.class, BsonInt32.class, (from) -> {
			return new Date(from.getValue());
		});
		register(Date.class, BsonInt64.class, (from) -> {
			return new Date(from.getValue());
		});
		register(Date.class, BsonString.class, (from) -> {
			return null;
		});
		register(Date.class, BsonTimestamp.class, (from) -> {
			return new Date(from.getTime() * 1000L);
		});
		register(Date.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(Date.class, Binary.class, (from) -> {
			return new Date(new BigInteger(from.getData()).longValue());
		});
		register(Date.class, Decimal128.class, (from) -> {
			return new Date(from.bigDecimalValue().longValue());
		});
		register(Date.class, Code.class, (from) -> {
			return new Date(from.getCode().hashCode());
		});
		register(Date.class, ObjectId.class, (from) -> {
			return new Date(from.hashCode());
		});
		register(Date.class, Symbol.class, (from) -> {
			return new Date(from.getSymbol().hashCode());
		});
		register(Date.class, BsonRegularExpression.class, (from) -> {
			return new Date(from.getPattern().hashCode());
		});

		// --- VALUE TO UUID CONVERTERS ---

		register(UUID.class, BsonNull.class, (from) -> {
			return null;
		});
		register(UUID.class, BsonBoolean.class, (from) -> {
			return new UUID(from.getValue() ? 1 : 0, 0);
		});
		register(UUID.class, BsonDateTime.class, (from) -> {
			return objectToUUID(from);
		});
		register(UUID.class, BsonDouble.class, (from) -> {
			return objectToUUID(from);
		});
		register(UUID.class, BsonInt32.class, (from) -> {
			return objectToUUID(from);
		});
		register(UUID.class, BsonInt64.class, (from) -> {
			return objectToUUID(from);
		});
		register(UUID.class, BsonString.class, (from) -> {
			return objectToUUID(from);
		});
		register(UUID.class, BsonTimestamp.class, (from) -> {
			return objectToUUID(from);
		});
		register(UUID.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(UUID.class, Binary.class, (from) -> {
			return byteArrayToUUID(from.getData());
		});
		register(UUID.class, Decimal128.class, (from) -> {
			return objectToUUID(from.bigDecimalValue());
		});
		register(UUID.class, Code.class, (from) -> {
			return objectToUUID(from.getCode().hashCode());
		});
		register(UUID.class, ObjectId.class, (from) -> {
			return objectToUUID(from.hashCode());
		});
		register(UUID.class, Symbol.class, (from) -> {
			return objectToUUID(from.getSymbol().hashCode());
		});
		register(UUID.class, BsonRegularExpression.class, (from) -> {
			return objectToUUID(from.getPattern().hashCode());
		});

		// --- VALUE TO INETADDRESS CONVERTERS ---

		register(InetAddress.class, BsonNull.class, (from) -> {
			return null;
		});
		register(InetAddress.class, BsonBoolean.class, (from) -> {
			return toInetAddress(from.getValue() ? 1 : 0);
		});
		register(InetAddress.class, BsonDateTime.class, (from) -> {
			return toInetAddress(from.getValue());
		});
		register(InetAddress.class, BsonDouble.class, (from) -> {
			return toInetAddress(from.getValue());
		});
		register(InetAddress.class, BsonInt32.class, (from) -> {
			return toInetAddress(from.getValue());
		});
		register(InetAddress.class, BsonInt64.class, (from) -> {
			return toInetAddress(from.getValue());
		});
		register(InetAddress.class, BsonString.class, (from) -> {
			return toInetAddress(from.getValue());
		});
		register(InetAddress.class, BsonTimestamp.class, (from) -> {
			return toInetAddress(from.getTime());
		});
		register(InetAddress.class, BsonUndefined.class, (from) -> {
			return null;
		});
		register(InetAddress.class, Binary.class, (from) -> {
			return toInetAddress(from.getData());
		});
		register(InetAddress.class, Decimal128.class, (from) -> {
			return toInetAddress(from.bigDecimalValue());
		});
		register(InetAddress.class, Code.class, (from) -> {
			return toInetAddress(from.getCode().hashCode());
		});
		register(InetAddress.class, ObjectId.class, (from) -> {
			return toInetAddress(from.hashCode());
		});
		register(InetAddress.class, Symbol.class, (from) -> {
			return toInetAddress(from.getSymbol().hashCode());
		});
		register(InetAddress.class, BsonRegularExpression.class, (from) -> {
			return toInetAddress(from.getPattern().hashCode());
		});

		// --- CLONING ---

		// Register immutable classes (for faster Tree cloning)
		DeepCloner.addImmutableClass(BsonBoolean.class, BsonDateTime.class, BsonDouble.class, BsonInt32.class,
				BsonInt64.class, BsonNull.class, BsonString.class, BsonTimestamp.class, BsonUndefined.class,
				Binary.class, Code.class, Decimal128.class, ObjectId.class, Symbol.class);

		// --- JSON FORMATTING ---

		DataConverterRegistry.addUnquotedClass(BsonBoolean.class, BsonDouble.class, BsonInt32.class, BsonInt64.class,
				Decimal128.class);
		if (!Config.USE_TIMESTAMPS) {
			DataConverterRegistry.addUnquotedClass(BsonTimestamp.class, BsonDateTime.class);
		}
	}

}