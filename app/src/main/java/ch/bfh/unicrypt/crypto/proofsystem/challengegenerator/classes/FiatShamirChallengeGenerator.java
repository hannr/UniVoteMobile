/*
 * UniCrypt
 *
 *  UniCrypt(tm) : Cryptographical framework allowing the implementation of cryptographic protocols e.g. e-voting
 *  Copyright (C) 2014 Bern University of Applied Sciences (BFH), Research Institute for
 *  Security in the Information Society (RISIS), E-Voting Group (EVG)
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniCrypt may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH), Research Institute for
 *   Security in the Information Society (RISIS), E-Voting Group (EVG)
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: unicrypt@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.unicrypt.crypto.proofsystem.challengegenerator.classes;

import ch.bfh.unicrypt.crypto.proofsystem.challengegenerator.abstracts.AbstractNonInteractiveChallengeGenerator;
import ch.bfh.unicrypt.helper.array.classes.ByteArray;
import ch.bfh.unicrypt.helper.converter.classes.biginteger.FiniteByteArrayToBigInteger;
import ch.bfh.unicrypt.helper.converter.interfaces.Converter;
import ch.bfh.unicrypt.helper.hash.HashMethod;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModElement;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import java.math.BigInteger;

public class FiatShamirChallengeGenerator
	   extends AbstractNonInteractiveChallengeGenerator<Set, Element, ZMod, ZModElement> {

	private final HashMethod hashMethod;
	private final Converter<ByteArray, BigInteger> converter;

	protected FiatShamirChallengeGenerator(Set inputSpace, ZMod challengeSpace, Element proverId, HashMethod hashMethod, Converter<ByteArray, BigInteger> converter) {
		super(inputSpace, challengeSpace, proverId);
		this.hashMethod = hashMethod;
		this.converter = converter;
	}

	public HashMethod getHashMethod() {
		return this.hashMethod;
	}

	public Converter<ByteArray, BigInteger> getConverter() {
		return this.converter;
	}

	@Override
	protected ZModElement abstractAbstractGenerate(Element input) {
		ByteArray hashedInput = input.getHashValue(this.hashMethod);
		return this.getChallengeSpace().getElement(this.converter.convert(hashedInput).mod(this.challengeSpace.getModulus()));
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace) {
		HashMethod hashMethod = HashMethod.getInstance();
		int length = hashMethod.getHashAlgorithm().getHashLength();
		return FiatShamirChallengeGenerator.getInstance(inputSpace, challengeSpace, (Element) null, hashMethod, FiniteByteArrayToBigInteger.getInstance(length));
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace, Element proverId) {
		HashMethod hashMethod = HashMethod.getInstance();
		int length = hashMethod.getHashAlgorithm().getHashLength();
		return FiatShamirChallengeGenerator.getInstance(inputSpace, challengeSpace, proverId, hashMethod, FiniteByteArrayToBigInteger.getInstance(length));
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace, HashMethod hashMethod) {
		int length = hashMethod.getHashAlgorithm().getHashLength();
		return FiatShamirChallengeGenerator.getInstance(inputSpace, challengeSpace, (Element) null, hashMethod, FiniteByteArrayToBigInteger.getInstance(length));
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace, Element proverId, HashMethod hashMethod) {
		int length = hashMethod.getHashAlgorithm().getHashLength();
		return FiatShamirChallengeGenerator.getInstance(inputSpace, challengeSpace, proverId, hashMethod, FiniteByteArrayToBigInteger.getInstance(length));
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace, Converter<ByteArray, BigInteger> converter) {
		return FiatShamirChallengeGenerator.getInstance(inputSpace, challengeSpace, (Element) null, HashMethod.getInstance(), converter);
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace, Element proverId, Converter<ByteArray, BigInteger> converter) {
		return FiatShamirChallengeGenerator.getInstance(inputSpace, challengeSpace, proverId, HashMethod.getInstance(), converter);
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace, HashMethod hashMethod, Converter<ByteArray, BigInteger> converter) {
		return FiatShamirChallengeGenerator.getInstance(inputSpace, challengeSpace, (Element) null, hashMethod, converter);
	}

	public static FiatShamirChallengeGenerator getInstance(final Set inputSpace, ZMod challengeSpace, Element proverId, HashMethod hashMethod, Converter<ByteArray, BigInteger> converter) {
		if (inputSpace == null || challengeSpace == null || hashMethod == null || converter == null) {
			throw new IllegalArgumentException();
		}
		return new FiatShamirChallengeGenerator(inputSpace, challengeSpace, proverId, hashMethod, converter);
	}

}
