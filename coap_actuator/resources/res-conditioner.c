#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"
#include "os/dev/button-hal.h"
#include "json_util.h"



#include <string.h>

#if PLATFORM_HAS_LEDS || LEDS_COUNT

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

static uint8_t conditioner_status =0;  //0 off, 1 on

//static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
//static void res_event_handler(void);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

/* A simple actuator example, depending on the color query parameter and post variable action, corresponding led is activated or deactivated */
RESOURCE(res_conditioner,
         "title=\"conditioner:?value=0|1 \" PUT action=<action> ;rt=\"Control\"",
         NULL,
         NULL,
         res_put_handler,
         NULL);



static void
res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  size_t len = 0;
  const char *threshold = NULL;
  const char *action=NULL;
  int success=1;
  const uint8_t *chunk;

  len = coap_get_payload(request,&chunk);

  printf("request_json:%s",(char*) chunk);

  /*
  // Create a JSON string from the chunk
  json_t *json = json_stringn((const char *)chunk, len);

  json_t *value_action=json_object_get(json, "action");

  action=json_string_value(value_action);

  json_t *value_threshold=json_object_get(json, "threshold");

  threshold=json_string_value(value_threshold);

  printf("action:%s",action);
  printf("threshold:%s",threshold);*/

 

  LOG_DBG("handler\n");

  if(len>0){
        action = get_json_value_string((char *)chunk, "action");
        LOG_INFO("received command: action=%s\n", action);
        threshold = get_json_value_string((char *)chunk, "threshold");
        LOG_INFO("received command: threshold=%s\n", threshold);
	} 

  if(threshold!=NULL && strlen(threshold)!=0) {
    LOG_DBG("value %.*s\n", (int)len, threshold);
    
    int value_int = atoi(threshold);
    if(value_int==1){
          //critic value of gas, and the action on led and conditioner are obliged
          leds_on(LEDS_RED);
          leds_off(LEDS_GREEN);
          LOG_INFO("start conditioner because gas value critic");
          //coap_set_status_code(response, CHANGED_2_04);
          leds_on(LEDS_YELLOW);
        
    }
    else{
        
          leds_on(LEDS_GREEN);
          leds_off(LEDS_RED);
          LOG_INFO("stop conditioner because gas value no critic");
          //coap_set_status_code(response, CHANGED_2_04);
          leds_off(LEDS_YELLOW);

          //critic value of gas are not critic, the user can also turn on or turn off the conditioner
          if ((action!=NULL && strlen(action)!=0)){
              LOG_DBG("action: %s\n", action);

              // action off 
              if (strncmp(action, "OFF", len) == 0 && conditioner_status==1){
                LOG_INFO("stop conditioner because user request");
                coap_set_status_code(response,CHANGED_2_04);
                leds_off(LEDS_YELLOW);
                conditioner_status=0;
                
              }
              // action on
              else if (strncmp(action, "ON", len) == 0 && conditioner_status==0){
                LOG_INFO("start conditioner because user request");
                coap_set_status_code(response,CHANGED_2_04);

                leds_on(LEDS_YELLOW);
                conditioner_status=1;
                
              }
              else{
                // action is a string different from off e on, or the action doesn't change the status of the conditioner
                  coap_set_status_code(response,BAD_OPTION_4_02);
              }
          }

        
    }
    success=1;
   
  } else {
    success = 0;
  } 

  

  if (!success){
    coap_set_status_code(response, BAD_REQUEST_4_00);
  }
}
#endif /* PLATFORM_HAS_LEDS */
