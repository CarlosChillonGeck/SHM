package daq;
 
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

/**
 * Partial adaptation from:
 * https://github.com/jrowberg/i2cdevlib/blob/master/MSP430/I2Cdev/I2Cdev.cpp
 */
public class I2Cdev {
	
	/** Read a single bit from an 8-bit device register.
	 * @param regAddr Register regAddr to read from
	 * @param bitNum Bit position to read (0-7)
	 * @return Single bit value
	 * @throws IOException 
	 */
	public static boolean readBit(I2CDevice i2c, int regAddr, int bitNum) throws IOException {
		int b = i2c.read(regAddr);
		
		// http://stackoverflow.com/a/14145767/196486
	    return ((b >> bitNum) & 1) == 1;
	}

	/** Write a single bit in an 8-bit device register.
	 * @param i2c I2C slave device
	 * @param regAddr Register regAddr to write to
	 * @param bitNum Bit position to write (0-7)
	 * @param value New bit value to write
	 * @return 
	 * @throws IOException 
	 */
	public static void writeBit(I2CDevice i2c, int regAddr, int bitNum, boolean value) throws IOException {
	    int b = i2c.read(regAddr);
	    b = value ? (b | (1 << bitNum)) : (b & ~(1 << bitNum));
	    i2c.write(regAddr, (byte) (b & 0xFF));
	}
	
	/** Read multiple bits from an 8-bit device register.
	 * @param regAddr Register regAddr to read from
	 * @param bitStart First bit position to read (0-7)
	 * @param length Number of bits to read (not more than 8)
	 * @return right-aligned value (i.e. '101' read from any bitStart position will equal 0x05)
	 * @throws IOException 
	 */
	public static int readBits(I2CDevice i2c, int regAddr, int bitStart, int length) throws IOException {
	    // 01101001 read byte
	    // 76543210 bit numbers
	    //    xxx   args: bitStart=4, length=3
	    //    010   masked
	    //   -> 010 shifted
		int b = i2c.read(regAddr);
		int mask = ((1 << length) - 1) << (bitStart - length + 1);
        b &= mask;
        b >>= (bitStart - length + 1);
	    return b;
	}
	
	/** Write multiple bits in an 8-bit device register.
	 * @param regAddr Register regAddr to write to
	 * @param bitStart First bit position to write (0-7)
	 * @param length Number of bits to write (not more than 8)
	 * @param data Right-aligned value to write
	 * @return 
	 * @throws IOException 
	 */
	public static void writeBits(I2CDevice i2c, int regAddr, int bitStart, int length, int data) throws IOException {
	    //      010 value to write
	    // 76543210 bit numbers
	    //    xxx   args: bitStart=4, length=3
	    // 00011100 mask byte
	    // 10101111 original value (sample)
	    // 10100011 original & ~mask
	    // 10101011 masked | value
		int b = i2c.read(regAddr);
        int mask = ((1 << length) - 1) << (bitStart - length + 1);
        data <<= (bitStart - length + 1); // shift data into correct position
        data &= mask; // zero all non-important bits in data
        b &= ~(mask); // zero all important bits in existing byte
        b |= data; // combine data with existing byte
        i2c.write(regAddr, (byte) (b & 0xFF));
	}
	
}