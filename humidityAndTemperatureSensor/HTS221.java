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
// - CTRL_REG3 functionality
// - STATUS_REG functionality


//package RPiModules;


// class that implements the functionality of the humidity and temperature in the HTS221
public class HTS221 extends I2C_Device {
  
  // PRIVATE VARIABLES
  
  // sensor registers addresses from datasheet
  private static final byte WHO_AM_I = (byte)0x0F;
  private static final byte who_am_i_value = (byte)0b10111100;
  private static final byte AV_CONF = (byte)0x10;
  private static final byte CTRL_REG1 = (byte)0x20;
  private static final byte CTRL_REG2 = (byte)0x21;
  private static final byte CTRL_REG3 = (byte)0x22;
  private static final byte STATUS_REG = (byte)0x27;
  private static final byte HUMIDITY_OUT_L = (byte)0x28;
  private static final byte HUMIDITY_OUT_H = (byte)0x29;
  private static final byte TEMP_OUT_L = (byte)0x2A;
  private static final byte TEMP_OUT_H = (byte)0x2B;
  
  
  // PUBLIC METHODS
  
  // creates a HTS221 object, and sets its address automatically
  public HTS221(){
    super();
    this.find_deviceAddress( this.who_am_i_value );
    if( this.get_deviceAddress() == -1 ){
      System.out.println( "HTS221: not able to find the device." );
    }else{
      System.out.println( "HTS221: found at " + Integer.toHexString( this.get_deviceAddress() ) );
    }
  }
  
  // creates a HTS221 object, and sets its address
  public HTS221( int i2cAddress ){
    super( i2cAddress );
    try{
      if( this.read_byteAt( this.WHO_AM_I ) != this.who_am_i_value ) System.out.println( "HTS221: not sure if " + Integer.toHexString( i2cAddress ) + " is the correct address. It seems to be other device." );
    }catch( Exception e ){
      System.out.println( "HTS221: not able to find the device in " + Integer.toHexString( i2cAddress ) );
    }
  }
  
  // sets the temperature resolution. Number of samples taken to average. The interval is [2,256] (see datasheet p.21)
  public void set_temperatureResolution( int numberOfAveragedSamples ){
    // first, we introduce the value in the interval
    int nSamples = ( 2 <= numberOfAveragedSamples )? numberOfAveragedSamples : 2;
    nSamples = ( numberOfAveragedSamples <= 256 )? numberOfAveragedSamples : 256;
    // we take n such that 2^n is the closest to the value in the interval
    byte value = (byte)Math.round( Math.log(nSamples)/Math.log(2.0) );
    value = (byte)(value - 1);  // the value we are looking for is a unit lower
    // we read the value of the register
    byte read = this.read_byteAt( this.AV_CONF );
    // we define the masks to compose the byte to write
    byte readMask = (byte)0b11000111;
    byte valueMask = (byte)0b00111000;
    // we compose the byte to write, and we write it
    byte toWrite = (byte)( ( read & readMask ) | ( (value<<3) & valueMask ) );
    this.write_byteAt( this.AV_CONF , toWrite );
  }
  
  // sets the humidity resolution. Number of samples taken to average. The interval is [4,512] (see datasheet p.21)
  public void set_humidityResolution( int numberOfAveragedSamples ){
    // first, we introduce the value in the interval
    int nSamples = ( 4 <= numberOfAveragedSamples )? numberOfAveragedSamples : 4;
    nSamples = ( numberOfAveragedSamples <= 512 )? numberOfAveragedSamples : 512;
    // we take the exponent of the power of 2 closest to the value in the interval
    byte value = (byte)Math.round( Math.log(nSamples)/Math.log(2.0) );
    value = (byte)(value - 2);  // the value we are looking for is two units lower
    // we read the value of the register
    byte read = this.read_byteAt( this.AV_CONF );
    // we define the masks to compose the byte to write
    byte readMask = (byte)0b11111000;
    byte valueMask = (byte)0b00000111;
    // we compose the byte to write, and we write it
    byte toWrite = (byte)( ( read & readMask ) | ( value & valueMask ) );
    this.write_byteAt( this.AV_CONF , toWrite );
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
  
  // sets the output data rate ( mode=0 -> one-shot;  mode=1 -> 1Hz;  mode=2 -> 7Hz;  mode=3 -> 12.5Hz.  See datasheet p.22)
  public void set_outputDataRate( int mode ){
    // we start reading the register
    byte read = this.read_byteAt( this.CTRL_REG1 );
    // we define the read mask
    byte readMask = (byte)0b11111100;
    // we define what to write depending on the mode
    byte toWrite;
    switch( mode ){
      case 0:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
      case 1:
        toWrite = (byte)( (read & readMask) | 0b00000001 );
        break;
      case 2:
        toWrite = (byte)( (read & readMask) | 0b00000010 );
        break;
      case 3:
        toWrite = (byte)( (read & readMask) | 0b00000011 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00000001 );
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
    // We wait 3 seconds for the operation to take place
    for(int i=0; i<3; i++){
      try{
        Thread.sleep(100);  // we wait a little for the device to reboot itself
      }catch( Exception e ){
        e.printStackTrace();
      }
      if( ( this.read_byteAt( this.CTRL_REG2 ) & 0b10000000 ) == 0b00000000 ){
        return;
      }
    }
    System.out.println( "[HTS221] Unable to reboot." );
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
  
  // enables the heater (see datasheet p.23)
  public void enable_heater(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG2 );
    // we change the bit to enable the heater
    reg |= 0b00000010;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG2 , reg );
  }
  
  // disables the heater (see datasheet p.23)
  public void disable_heater(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG2 );
    // we change the bit to disable the heater
    reg &= 0b11111101;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG2 , reg );
  }
  
  // gets humidity data
  public short get_humidityData(){
    // we read the two bytes (I have no idea why one have to add 0x80, but I have seen it in another code, and if it is not done it does not work)
    byte[] buffer = this.read_bytesAt( (byte)( this.HUMIDITY_OUT_L + 0x80 )  , 2 );
    // we compose the two bytes to return them as a short
    return (short)( ( buffer[1] << 8 ) | (buffer[0] & 0xFF) );
  }
  
  // gets temperature data
  public short get_temperatureData(){
    // we read the two bytes (I have no idea why one have to add 0x80, but I have seen it in another code, and if it is not done it does not work)
    byte[] buffer = this.read_bytesAt( (byte)( this.TEMP_OUT_L + 0x80 ) , 2 );
    // we compose the two bytes to return them as a short
    return (short)( ( buffer[1] << 8 ) | (buffer[0] & 0xFF) );
  }
  
}
