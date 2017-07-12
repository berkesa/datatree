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
package io.datatree.dom;

import java.util.Base64;

/**
 * BASE64 encoder / decoder. To use custom / faster implementation, set the
 * "datatree.base64.codec" System Property:<br>
 * <br>
 * -Ddatatree.base64.codec=your.base64.Codec
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class BASE64 {

	// --- COMMON BASE64 CODEC ---

	public static BASE64Codec codec;

	static {
		try {
			if (Config.BASE64_CODEC != null) {
				codec = (BASE64Codec) Class.forName(Config.BASE64_CODEC).newInstance();
			}
		} catch (Exception cause) {
			cause.printStackTrace();
		} finally {
			if (codec == null) {
				codec = new BASE64Codec() {
					
					private final Base64.Encoder encoder = Base64.getEncoder();
					private final Base64.Decoder decoder = Base64.getDecoder();

					@Override
					public String encode(byte[] src) {
						return encoder.encodeToString(src);
					}

					@Override
					public byte[] decode(String src) {
						return decoder.decode(src);
					}
					
				};
			}
		}
	}

	// --- ENCODER ---

	public static final String encode(byte[] src) {
		return codec.encode(src);
	}

	// --- DECODER ---

	public static final byte[] decode(String src) {
		return codec.decode(src);
	}

}