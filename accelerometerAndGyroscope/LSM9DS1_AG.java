/*
 * Copyright (C) 2017 P.Bernal-Polo
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
// - ACT_THS
// - ACT_DUR
// - INT_GEN_CFG_XL && INT_GEN_THS_X_XL && INT_GEN_THS_Y_XL && INT_GEN_THS_Z_XL && INT_GEN_DUR_XL
// - REFERENCE_G
// - INT1_CTRL && INT2_CTRL
// - CTRL_REG2_G (LPF && HPF)
// - CTRL_REG3_G (Low-power mode &&& HPF)
// - ORIENT_CFG_G
// - INT_GEN_SRC_G
// - CTRL_REG4
// - CTRL_REG5_XL
// - CTRL_REG7_XL
// - some in CTRL_REG8
// - CTRL_REG9
// - CTRL_REG10
// - INT_GEN_SRC_XL
// - STATUS_REG
// - FIFO_CTRL
// - FIFO_SRC
// - INT_GEN_CFG_G && INT_GEN_THS_X_G && INT_GEN_THS_Y_G && INT_GEN_THS_Z_G && INT_GEN_DUR_G


//package RPiModules;


// class that implements the functionality of the accelerometer and the gyroscope in the LSM9DS1
public class LSM9DS1_AG extends I2C_Device {
  
  // PRIVATE VARIABLES
  
  // accelerometer and gyroscope registers addresses from datasheet
  private static final byte ACT_THS = (byte)0x04;
  private static final byte ACT_DUR = (byte)0x05;
  private static final byte INT_GEN_CFG_XL = (byte)0x06;
  private static final byte INT_GEN_THS_X_XL = (byte)0x07;
  private static final byte INT_GEN_THS_Y_XL = (byte)0x08;
  private static final byte INT_GEN_THS_Z_XL = (byte)0x09;
  private static final byte INT_GEN_DUR_XL = (byte)0x0A;
  private static final byte REFERENCE_G = (byte)0x0B;
  private static final byte INT1_CTRL = (byte)0x0C;
  private static final byte INT2_CTRL = (byte)0x0D;
  private static final byte WHO_AM_I = (byte)0x0F;
  private static final byte who_am_i_value = (byte)0b01101000;
  private static final byte CTRL_REG1_G = (byte)0x10;
  private static final byte CTRL_REG2_G = (byte)0x11;
  private static final byte CTRL_REG3_G = (byte)0x12;
  private static final byte ORIENT_CFG_G = (byte)0x13;
  private static final byte INT_GEN_SRC_G = (byte)0x14;
  private static final byte OUT_TEMP_L = (byte)0x15;
  private static final byte OUT_TEMP_H = (byte)0x16;
  private static final byte STATUS_REG1 = (byte)0x17;
  private static final byte OUT_X_G_L = (byte)0x18;
  private static final byte OUT_X_G_H = (byte)0x19;
  private static final byte OUT_Y_G_L = (byte)0x1A;
  private static final byte OUT_Y_G_H = (byte)0x1B;
  private static final byte OUT_Z_G_L = (byte)0x1C;
  private static final byte OUT_Z_G_H = (byte)0x1D;
  private static final byte CTRL_REG4 = (byte)0x1E;
  private static final byte CTRL_REG5_XL = (byte)0x1F;
  private static final byte CTRL_REG6_XL = (byte)0x20;
  private static final byte CTRL_REG7_XL = (byte)0x21;
  private static final byte CTRL_REG8 = (byte)0x22;
  private static final byte CTRL_REG9 = (byte)0x23;
  private static final byte CTRL_REG10 = (byte)0x24;
  private static final byte INT_GEN_SRC_XL = (byte)0x26;
  private static final byte STATUS_REG2 = (byte)0x27;
  private static final byte OUT_X_XL_L = (byte)0x28;
  private static final byte OUT_X_XL_H = (byte)0x29;
  private static final byte OUT_Y_XL_L = (byte)0x2A;
  private static final byte OUT_Y_XL_H = (byte)0x2B;
  private static final byte OUT_Z_XL_L = (byte)0x2C;
  private static final byte OUT_Z_XL_H = (byte)0x2D;
  private static final byte FIFO_CTRL = (byte)0x2E;
  private static final byte FIFO_SRC = (byte)0x2F;
  private static final byte INT_GEN_CFG_G = (byte)0x30;
  private static final byte INT_GEN_THS_X_G = (byte)0x31;
  private static final byte INT_GEN_THS_Y_G = (byte)0x33;
  private static final byte INT_GEN_THS_Z_G = (byte)0x35;
  private static final byte INT_GEN_DUR_G = (byte)0x37;
  
  
  // PUBLIC METHODS
  
  // creates a LSM9DS1 object, and sets its addresses automatically
  public LSM9DS1_AG(){
    super();
    this.find_deviceAddress( this.who_am_i_value );
    if( this.get_deviceAddress() == -1 ){
      System.out.println( "LSM9DS1: not able to find accelGyro." );
    }else{
      System.out.println( "LSM9DS1: accelGyro found at " + Integer.toHexString( this.get_deviceAddress() ) );
    }
  }
  
  // creates a LSM9DS1 object, and sets its addresses
  public LSM9DS1_AG( int i2cAddress ){
    super( i2cAddress );
    try{
      if( this.read_byteAt( this.WHO_AM_I ) != this.who_am_i_value ) System.out.println( "LSM9DS1: not sure if " + Integer.toHexString( i2cAddress ) + " is the accelGyro address. It seems to be other device." );
    }catch( Exception e ){
      System.out.println( "LSM9DS1: not able to find accelGyro in " + Integer.toHexString( i2cAddress ) );
    }
  }
  
  // sets the measurement range of the accelerometer ( mode=0 -> 2g;  mode=1 -> 4g;  mode=2 -> 8g;  mode=3 -> 16g )
  public void set_accelMeasurementRange( int mode ){
    // we start reading the register
    byte read = this.read_byteAt( this.CTRL_REG6_XL );
    // we define the read mask
    byte readMask = (byte)0b11100111;
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
        toWrite = (byte)( (read & readMask) | 0b00011000 );
        break;
      case 3:
        toWrite = (byte)( (read & readMask) | 0b00001000 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
    }
    // and we write it
    this.write_byteAt( this.CTRL_REG6_XL , toWrite );
    
    return;
  }
  
  // sets the measurement range of the gyroscope ( mode=0 -> 245dps;  mode=1 -> 500dps;  mode=2 -> 2000dps )
  public void set_gyroMeasurementRange( int mode ){
    // we start reading the register
    byte read = this.read_byteAt( this.CTRL_REG1_G );
    // we define the read mask
    byte readMask = (byte)0b11100111;
    // we define what to write depending on the mode
    byte toWrite;
    switch( mode ){
      case 0:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
      case 1:
        toWrite = (byte)( (read & readMask) | 0b00001000 );
        break;
      case 2:
        toWrite = (byte)( (read & readMask) | 0b00011000 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
    }
    // and we write it
    this.write_byteAt( this.CTRL_REG1_G , toWrite );
    
    return;
  }
  
  // sets the output data rate of the accelerometer ( mode=0 -> 0Hz;  mode=1 -> 10Hz;  mode=2 -> 50Hz;  mode=3 -> 119Hz;  mode=4 -> 238Hz;  mode=5 -> 476Hz;  mode=6 -> 952Hz )
  public void set_accelDataRate( int mode ){
    // we start reading the register
    byte read = this.read_byteAt( this.CTRL_REG6_XL );
    // we define the read mask
    byte readMask = (byte)0b00011111;
    // we define what to write depending on the mode
    byte toWrite;
    switch( mode ){
      case 0:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
      case 1:
        toWrite = (byte)( (read & readMask) | 0b00100000 );
        break;
      case 2:
        toWrite = (byte)( (read & readMask) | 0b01000000 );
        break;
      case 3:
        toWrite = (byte)( (read & readMask) | 0b01100000 );
        break;
      case 4:
        toWrite = (byte)( (read & readMask) | 0b10000000 );
        break;
      case 5:
        toWrite = (byte)( (read & readMask) | 0b10100000 );
        break;
      case 6:
        toWrite = (byte)( (read & readMask) | 0b11000000 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00100000 );
        break;
    }
    // and we write it
    this.write_byteAt( this.CTRL_REG6_XL , toWrite );
    
    return;
  }
  
  // sets the output data rate of the gyroscope ( mode=0 -> 0Hz;  mode=1 -> 14.9Hz;  mode=2 -> 59.5Hz;  mode=3 -> 119Hz;  mode=4 -> 238Hz;  mode=5 -> 476Hz;  mode=6 -> 952Hz )
  public void set_gyroDataRate( int mode ){
    // we start reading the register
    byte read = this.read_byteAt( this.CTRL_REG1_G );
    // we define the read mask
    byte readMask = (byte)0b00011100;
    // we define what to write depending on the mode
    byte toWrite;
    switch( mode ){
      case 0:
        toWrite = (byte)( (read & readMask) | 0b00000011 );
        break;
      case 1:
        toWrite = (byte)( (read & readMask) | 0b00100011 );
        break;
      case 2:
        toWrite = (byte)( (read & readMask) | 0b01000011 );
        break;
      case 3:
        toWrite = (byte)( (read & readMask) | 0b01100011 );
        break;
      case 4:
        toWrite = (byte)( (read & readMask) | 0b10000011 );
        break;
      case 5:
        toWrite = (byte)( (read & readMask) | 0b10100011 );
        break;
      case 6:
        toWrite = (byte)( (read & readMask) | 0b11000011 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00100011 );
        break;
      // we always set the highest cut-off frequency for the Low-Pass Filter
    }
    // and we write it
    this.write_byteAt( this.CTRL_REG1_G , toWrite );
    
    return;
  }
  
  // reboots the memory content
  public void reboot(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG8 );
    // we change the bit to reboot the device
    reg |= 0b10000000;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG8 , reg );
    try{
      Thread.sleep(1000);  // we wait a little for the device to reboot itself
    }catch( Exception e ){ }
    
    return;
  }
  
  // resets the device using software
  public void reset(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG8 );
    // we change the bit to reboot the device
    reg |= 0b00000001;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG8 , reg );
    try{
      Thread.sleep(1000);  // we wait a little for the device to reset itself
    }catch( Exception e ){ }
    
    return;
  }
  
  // sets a continuous data update (which means that you could be reading the MSB and, before reading the LSB, the value could change)
  public void set_continuousDataUpdate(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG8 );
    // we change the bit to set a continuous data update
    reg &= 0b10111111;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG8 , reg );
    
    return;
  }
  
  // sets a non-continuous data update (this feature prevents the reading of LSB and MSB related to different samples)
  public void set_nonContinuousDataUpdate(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG8 );
    // we change the bit to set a non-continuous data update
    reg |= 0b01000000;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG8 , reg );
    
    return;
  }
  
  // gets temperature data
  public short get_temperatureData(){
    // we read the two bytes
    byte[] buffer = this.read_bytesAt( this.OUT_TEMP_L , 2 );
    // we compose the two bytes to return them as a short
    return (short)( ( buffer[1] << 8 ) | (buffer[0] & 0xFF) );
  }
  
  // gets accelerometer data
  public short[] get_accelData(){
    // we read the bytes
    byte[] buffer = this.read_bytesAt( this.OUT_X_XL_L , 6 );
    // we compose each pair of bytes to return them as shorts (we do -y so that the reference system is right-handed)
    return new short[]{  (short)( ( buffer[1] << 8 ) | (buffer[0] & 0xFF) )  ,  (short)-( ( buffer[3] << 8 ) | (buffer[2] & 0xFF) )  ,  (short)( ( buffer[5] << 8 ) | (buffer[4] & 0xFF) )  };
  }
  
  // gets gyroscope data
  public short[] get_gyroData(){
    // we read the bytes
    byte[] buffer = this.read_bytesAt( this.OUT_X_G_L , 6 );
    // we compose each pair of bytes to return them as shorts (we do -y so that the reference system is right-handed)
    return new short[]{  (short)( ( buffer[1] << 8 ) | (buffer[0] & 0xFF) )  ,  (short)-( ( buffer[3] << 8 ) | (buffer[2] & 0xFF) )  ,  (short)( ( buffer[5] << 8 ) | (buffer[4] & 0xFF) )  };
  }
  
}
