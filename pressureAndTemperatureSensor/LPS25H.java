/*
 * Copyright (C) 2019 P.Bernal-Polo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author P.Bernal-Polo
 */

// TODO:
// - set_pressureReference( int pressureRef )
// - DIFF_EN: Interrupt circuit enable
// - RESET_AZ
// - AUTO_ZERO
// - CTRL_REG3
// - CTRL_REG4
// - INTERRUPT_CFG
// - INT_SOURCE
// - STATUS_REG
// - FIFO_CTRL
// - FIFO_STATUS
// - THS_P_L && THS_P_H && RPDS_L && RPDS_H


//package RPiModules;


// class that implements the functionality of the pressure and temperature sensor in the LPS25H
public class LPS25H extends I2C_Device {
  
  // PRIVATE VARIABLES
  
  // sensor registers addresses from datasheet
  private static final byte REF_P_XL = (byte)0x08;
  private static final byte REF_P_L = (byte)0x09;
  private static final byte REF_P_H = (byte)0x0A;
  private static final byte WHO_AM_I = (byte)0x0F;
  private static final byte who_am_i_value = (byte)0b10111101;
  private static final byte RES_CONF = (byte)0x10;
  private static final byte CTRL_REG1 = (byte)0x20;
  private static final byte CTRL_REG2 = (byte)0x21;
  private static final byte CTRL_REG3 = (byte)0x22;
  private static final byte CTRL_REG4 = (byte)0x23;
  private static final byte INTERRUPT_CFG = (byte)0x24;
  private static final byte INT_SOURCE = (byte)0x25;
  private static final byte STATUS_REG = (byte)0x27;
  private static final byte PRESS_OUT_XL = (byte)0x28;
  private static final byte PRESS_OUT_L = (byte)0x29;
  private static final byte PRESS_OUT_H = (byte)0x2A;
  private static final byte TEMP_OUT_L = (byte)0x2B;
  private static final byte TEMP_OUT_H = (byte)0x2C;
  private static final byte FIFO_CTRL = (byte)0x2E;
  private static final byte FIFO_STATUS = (byte)0x2F;
  private static final byte THS_P_L = (byte)0x30;
  private static final byte THS_P_H = (byte)0x31;
  private static final byte RPDS_L = (byte)0x39;
  private static final byte RPDS_H = (byte)0x3A;
  
  
  // PUBLIC METHODS
  
  // creates a LPS25H object, and sets its address automatically
  public LPS25H(){
    super();
    this.find_deviceAddress( this.who_am_i_value );
    if( this.get_deviceAddress() == -1 ){
      System.out.println( "LPS25H: not able to find the device." );
    }else{
      System.out.println( "LPS25H: found at " + Integer.toHexString( this.get_deviceAddress() ) );
    }
  }
  
  // creates a LPS25H object, and sets its address
  public LPS25H( int i2cAddress ){
    super( i2cAddress );
    try{
      if( this.read_byteAt( this.WHO_AM_I ) != this.who_am_i_value ) System.out.println( "LPS25H: not sure if " + Integer.toHexString( i2cAddress ) + " is the correct address. It seems to be other device." );
    }catch( Exception e ){
      System.out.println( "LPS25H: not able to find the device in " + Integer.toHexString( i2cAddress ) );
    }
  }
  
  // sets the pressure resolution. Number of samples taken to average. The interval is [8,512] (see datasheet p.24)
  public void set_pressureResolution( int numberOfAveragedSamples ){
    // first, we introduce the value in the interval
    int nSamples = ( 8 <= numberOfAveragedSamples )? numberOfAveragedSamples : 8;
    nSamples = ( numberOfAveragedSamples <= 512 )? numberOfAveragedSamples : 512;
    // we take n such that 2^n is the closest to the value in the interval
    byte value = (byte)Math.round( Math.log(nSamples)/Math.log(2.0) );
    if( ( value & 1 ) == 0 ) value -= 1;  // if the result n is even, then we take the odd value below
    value = (byte)(value - 3);  // the value we are looking for is three units lower
    // we read the value of the register
    byte read = this.read_byteAt( this.RES_CONF );
    // we define the masks to compose the byte to write
    byte readMask = (byte)0b11111100;
    byte valueMask = (byte)0b00000011;
    // we compose the byte to write, and we write it
    byte toWrite = (byte)( ( read & readMask ) | ( value & valueMask ) );
    this.write_byteAt( this.RES_CONF , toWrite );
  }
  
  // sets the temperature resolution. Number of samples taken to average. The interval is [8,64] (see datasheet p.24)
  public void set_temperatureResolution( int numberOfAveragedSamples ){
    // first, we introduce the value in the interval
    int nSamples = ( 8 <= numberOfAveragedSamples )? numberOfAveragedSamples : 8;
    nSamples = ( numberOfAveragedSamples <= 64 )? numberOfAveragedSamples : 64;
    // we take n such that 2^n is the closest to the value in the interval
    byte value = (byte)Math.round( Math.log(nSamples)/Math.log(2.0) );
    value = (byte)(value - 3);  // the value we are looking for is three units lower
    // we read the value of the register
    byte read = this.read_byteAt( this.RES_CONF );
    // we define the masks to compose the byte to write
    byte readMask = (byte)0b11110011;
    byte valueMask = (byte)0b00001100;
    // we compose the byte to write, and we write it
    byte toWrite = (byte)( ( read & readMask ) | ( (value<<2) & valueMask ) );
    this.write_byteAt( this.RES_CONF , toWrite );
  }
  
  // activates the device
  public void powerOn(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG1 );
    // we change the bit to activate the device
    reg |= 0b10000000;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG1 , reg );
    try{
      Thread.sleep(100);  // we wait a little for the device to power-on
    }catch( Exception e ){
      e.printStackTrace();
    }
  }
  
  // deactivates the device
  public void powerOff(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG1 );
    // we change the bit to deactivate the device
    reg &= 0b01111111;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG1 , reg );
    try{
      Thread.sleep(100);  // we wait a little for the device to power-off
    }catch( Exception e ){
      e.printStackTrace();
    }
  }
  
  // sets a continuous data update (which means that you could be reading the MSB and, before reading the LSB, the value could change)
  public void set_continuousDataUpdate(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG1 );
    // we change the bit to set a continuous data update
    reg &= 0b11111011;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG1 , reg );
  }
  
  // sets a non-continuous data update (this feature prevents the reading of LSB and MSB related to different samples)
  public void set_nonContinuousDataUpdate(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG1 );
    // we change the bit to set a non-continuous data update
    reg |= 0b00000100;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG1 , reg );
  }
  
  // sets the output data rate ( mode=0 -> one-shot;  mode=1 -> 1Hz;  mode=2 -> 7Hz;  mode=3 -> 12.5Hz;  mode=4 -> 25Hz.  See datasheet p.25)
  public void set_outputDataRate( int mode ){
    // we start reading the register
    byte read = this.read_byteAt( this.CTRL_REG1 );
    // we define the read mask
    byte readMask = (byte)0b10001111;
    // we define what to write depending on the mode
    byte toWrite;
    switch( mode ){
      case 0:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
      case 1:
        toWrite = (byte)( (read & readMask) | 0b00010000 );
        break;
      case 2:
        toWrite = (byte)( (read & readMask) | 0b00100000 );
        break;
      case 3:
        toWrite = (byte)( (read & readMask) | 0b00110000 );
        break;
      case 4:
        toWrite = (byte)( (read & readMask) | 0b01000000 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00010000 );
        break;
    }
    // and we write it
    this.write_byteAt( this.CTRL_REG1 , toWrite );
  }
  
  // reboots the memory content
  public void reboot(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG2 );
    // we change the bit to reboot the device
    reg |= 0b10000000;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG2 , reg );
    try{
      Thread.sleep(100);  // we wait a little for the device to reboot itself
    }catch( Exception e ){
      e.printStackTrace();
    }
  }
  
  // resets the device using software
  public void reset(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG2 );
    // we change the bit to reboot the device
    reg |= 0b00000100;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG2 , reg );
    try{
      Thread.sleep(1000);  // we wait a little for the device to reset itself
    }catch( Exception e ){
      e.printStackTrace();
    }
  }
  
  // updates the measurements (only for  mode=0 -> one-shot  in set_outputDataRate( mode ) )
  public void update_measurements(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG2 );
    // we change the bit to update the measurements
    reg |= 0b00000001;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG2 , reg );
  }
  
  // gets pressure data
  public int get_pressureData(){
    // we read the three bytes (I have no idea why one have to add 0x80, but I have seen it in another code, and if it is not done it does not work)
    byte[] buffer = this.read_bytesAt( (byte)( this.PRESS_OUT_XL + 0x80 ) , 3 );
    // we compose the three bytes to return them as an int
    return ( ( (buffer[2] & 0xFF) << 16 ) | ( (buffer[1] & 0xFF) << 8 ) | (buffer[0] & 0xFF) );
  }
  
  // gets temperature data
  public short get_temperatureData(){
    // we read the two bytes (I have no idea why one have to add 0x80, but I have seen it in another code, and if it is not done it does not work)
    byte[] buffer = this.read_bytesAt( (byte)( this.TEMP_OUT_L + 0x80 ) , 2 );
    // we compose the two bytes to return them as a short
    return (short)( ( (buffer[1] & 0xFF) << 8 ) | (buffer[0] & 0xFF) );
  }
  
  
  
  
  
  
  
  
  
  
  
}
