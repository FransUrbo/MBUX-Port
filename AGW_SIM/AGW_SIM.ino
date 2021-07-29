#include <Arduino.h>
#include "mcp2515.h"

// Change these 2 defines if your CS pins are different
// They CANNOT be the same
#define CANB_CS 4
#define CANC_CS 5

MCP2515* canC;
MCP2515* canB;

can_frame read = {0x00};

void printFrame() {
    char buf[100] = {0x00};
    uint8_t pos = 0;
    pos += sprintf(buf, "ID: %04X - ", read.can_id);
    for (int i = 0; i < read.can_dlc; i++) {
        pos += sprintf(buf + pos, "%02X ", read.data[i]);
    }
    Serial.println(buf);
}

bool hasFrame = false;
void setup()
{
    Serial.begin(115200);
    canB = new MCP2515(CANB_CS);
	  canB->reset();
    canB->setBitrate(CAN_83K3BPS);
    canB->setNormalMode();
}

void loop()
{
	if (canB->readMessage(&read) == MCP2515::ERROR_OK) {
        bool awake = true;
        if (awake && !hasFrame) {
            hasFrame = true;
            read.can_dlc = 8;
            read.can_id = 0x01D0;
            read.data[0] = 0x03;
            read.data[1] = 0x05;
            read.data[2] = 0x20;
            read.data[3] = 0x06;
            canB->sendMessage(&read);
            Serial.print("Send ");
            printFrame();
        }
        if (read.can_id != 0x01D0) {
            Serial.print("Recv ");
            printFrame();
        }
    }
}
