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

/**
 * WriterFunction function with an optional exception.
 * 
 * @param <I> Input type
 * @param <O> Output type
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@FunctionalInterface
public interface WriterFunction<I, O> {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param input
	 *            the function argument
	 * 
	 * @return the function result
	 * 
	 * @throws Exception
	 *             any mapping Exception
	 */
	O apply(I input) throws Exception;

}