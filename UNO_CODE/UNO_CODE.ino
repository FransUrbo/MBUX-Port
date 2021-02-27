 
#include <Arduino.h>
#include "mcp2515.h"


#define RGB // Uncomment if your setup does not include RGB

#ifdef RGB
#include "rgb.h"
#endif


// Change these 2 defines if your CS pins are different
// They CANNOT be the same
#define CANB_CS 4
#define CANC_CS 5

MCP2515* canC;
MCP2515* canB;

RGB_Manager *rgb;

// IO struct with the tablet
// It is a compressed CAN Frame
struct tablet_frame {
    char can_bus_id;
    uint16_t id; // First 2 bytes for these cars are always 0000, so ignore
    uint8_t dlc;
    uint8_t data[8];
};

const uint8_t FRAME_SIZE = sizeof(tablet_frame);

tablet_frame io_frame = {0x00}; // Reserve in memory
can_frame io_can_frame = {0x00}; // Reserve this in memory as well

void setup() {
    Serial.begin(115200);

    pinMode(13, OUTPUT);

    // Init the CAN modules
    canB = new MCP2515(CANB_CS);
    canC = new MCP2515(CANC_CS);
    canB->reset();
    canC->reset();
    canB->setBitrate(CAN_83K3BPS);
    canC->setBitrate(CAN_500KBPS);
    // I trust myself to write to CAN B
    // Set it as Read + Write
    canB->setNormalMode();

    // I don't trust myself to write to CAN C
    // Set it as read only!
    canC->setListenOnlyMode(); 

    // Setup the RGB!
#ifdef RGB
    rgb = new RGB_Manager();
    rgb->write_channel(4, 255, 0, 0, 0);
#endif

    // TODO - Filtering - We don't need EVERY message on either bus!
}

char writeBuf[30]={0x00};
void writeFrame(char bus_id, can_frame* f) {
    memset(writeBuf, 0x00, sizeof(writeBuf));
    uint8_t pos = 0;
    pos += sprintf(writeBuf, "%c%04X", bus_id, f->can_id);
    for (int i = 0; i < f->can_dlc; i++) {
        pos+=sprintf(writeBuf+pos, "%02X", f->data[i]);
    }
    Serial.print(writeBuf);
    Serial.print('\n');
}

uint32_t last_sent_b = 0xFFFF;
uint32_t last_sent_c = 0xFFFF;
bool w = false;

unsigned long last_update_millis = millis();
void loop() {
    // Incomming request from tablet! - Send to CAN
    if (Serial.available() >= FRAME_SIZE) {
        Serial.readBytes((char*)&io_frame, FRAME_SIZE);
        io_can_frame.can_id = io_frame.id;
        io_can_frame.can_dlc = io_frame.dlc;
        memcpy(io_can_frame.data, io_frame.data, io_frame.dlc);
        switch (io_frame.can_bus_id)
        {

#ifdef RGB
        case 'D':
            // 'D' frames are special as they are commands for the RGB Subsystem
            // Byte 0 - Channel ID 
            // Byte 1 - step count for fade (Each step is 10ms)
            // Byte 2 - R
            // Byte 3 - G
            // Byte 4 - B
            rgb->write_channel(io_can_frame.data[0], io_can_frame.data[1], io_can_frame.data[2], io_can_frame.data[3], io_can_frame.data[4]);
            break;
#endif
        case 'C':
            canC->sendMessage(&io_can_frame);
            last_sent_c = io_can_frame.can_id;
            break;
        case 'B':
            canB->sendMessage(&io_can_frame);
            last_sent_b = io_can_frame.can_id;
            break;
        default:
            break;
        }
    }
    // Poll for any new CAN frames on Bus B
    if (canB->readMessage(&io_can_frame) == MCP2515::ERROR_OK) {
        if (io_can_frame.can_id != last_sent_b) {
            writeFrame('B', &io_can_frame);
        }
    }
    // Poll for any new CAN frames on Bus C
    if (canC->readMessage(&io_can_frame) == MCP2515::ERROR_OK) {
        if (io_can_frame.can_id != last_sent_c) {
            writeFrame('C', &io_can_frame);
        }
    }
    if (millis() >= last_update_millis + 10) {
        last_update_millis = millis();
        rgb->update();
    }
}