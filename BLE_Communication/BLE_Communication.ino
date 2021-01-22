// BN: Arduino Nano 33 BLE
// VID: 0x2341
// PID: 0x805a
// SN: 88F69516B1B6F281

#include <ArduinoBLE.h>
#define LED_PIN 13
#define WRITE_BUFFER_SIZE 256
#define WRITE_BUFFER_FIXED_LENGTH false

const char* nameOfPeripheral = "HandController";
const char* uuidOfService    = "00001101-0000-1000-8000-00805f9b34fb";
const char* uuidOfRxChar     = "00001142-0000-1000-8000-00805f9b34fb";
const char* uuidOfTxChar     = "00001143-0000-1000-8000-00805f9b34fb";

BLEService handService(uuidOfService);
BLECharacteristic rxChar(uuidOfRxChar, BLEWriteWithoutResponse | BLEWrite, WRITE_BUFFER_SIZE, WRITE_BUFFER_FIXED_LENGTH);
BLEByteCharacteristic txChar(uuidOfTxChar, BLERead | BLENotify | BLEBroadcast);

//short sampleBuffer[256];

//volatile int samplesRead;

void setup() {
  // Start Serial
  Serial.begin(9600);

  // Ensure Serial port is ready
  //while(!Serial);

  //Prepare LED
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  pinMode(A0, OUTPUT);
  //pinMode(LEDR, OUTPUT);
  //pinMode(LEDG, OUTPUT);

  // Start the BLE module
  startBLE();

  BLE.setLocalName(nameOfPeripheral);
  BLE.setAdvertisedService(handService);
  handService.addCharacteristic(rxChar);
  handService.addCharacteristic(txChar);
  BLE.addService(handService);

  BLE.setEventHandler(BLEConnected, onBLEConnect);
  BLE.setEventHandler(BLEDisconnected, onBLEDisconnect);

  rxChar.setEventHandler(BLEWritten, onRxCharValueUpdate);

  BLE.advertise();

  Serial.println("Peripheral Advertising Info: ");
  Serial.print("Name:         "); Serial.println(nameOfPeripheral);
  Serial.print("MAC:          "); Serial.println(BLE.address());
  Serial.print("Service UUID: "); Serial.println(handService.uuid());
  Serial.print("RxChar  UUID: "); Serial.println(uuidOfRxChar);
  Serial.print("TxChar  UUID: "); Serial.println(uuidOfTxChar);

  Serial.println("Bluetooth device active. Waiting for connections...");
}

void loop() {
  BLEDevice central = BLE.central();

  if(central)
  {
    // Only send data if connected to central device
    while(central.connected())
    {
      connectedLight();

      // Arduino -> Phone communication code goes here
      // This is just example code
      /*if(samplesRead)
      {
        // print samples to serial monitor / plotter
        for(int i = 0; i < samplesRead; i++)
        {
          txChar.writeValue(sampleBuffer[i]);
        }
        //clear read count
        samplesRead = 0;
      }*/
    }
  }
  else
  {
    disconnectedLight();
  }
}

/*
 * BLUETOOTH FUNCTIONS
 */

void startBLE()
{
  if(!BLE.begin())
  {
    Serial.println("Starting BLE failed!");
    while(1);
  }
}

void onBLEConnect(BLEDevice central)
{
  Serial.print("Connected event, central: "); Serial.println(central.address());

  connectedLight();
}

void onBLEDisconnect(BLEDevice central)
{
  Serial.print("Disconnected event, central: "); Serial.println(central.address());

  disconnectedLight();
}

void onRxCharValueUpdate(BLEDevice central, BLECharacteristic characteristic)
{
  Serial.print("Characteristic event, read: ");
  byte test[256];
  int dataLength = rxChar.readValue(test, 256);

  for(int i = 0; i < dataLength; i++)
  {
    Serial.print((char)test[i]);
  }
  Serial.println();
  Serial.print("Value length: "); Serial.println(rxChar.valueLength());
}

/*
 * LED FUNCTIONS
 */

void connectedLight()
{
  digitalWrite(LED_BUILTIN, HIGH);
  digitalWrite(LED_PIN, HIGH);
  digitalWrite(A0, LOW);
}

void disconnectedLight()
{
  digitalWrite(LED_BUILTIN, LOW);
  digitalWrite(LED_PIN, LOW);
  digitalWrite(A0, HIGH);
}
