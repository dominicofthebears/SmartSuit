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

static uint8_t shielding_status =0;  //0 off, 1 on

//static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
//static void res_event_handler(void);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

/* A simple actuator example, depending on the color query parameter and post variable action, corresponding led is activated or deactivated */
RESOURCE(res_radiation,
         "title=\"radiation:?value=0|1 \" PUT action=<action> ;rt=\"Control\"",
         NULL,
         NULL,
         res_put_handler,
         NULL);

//static int last_value=0;


/*static void
res_event_handler(void)
{
    // Notify all the observers
    coap_notify_observers(&res_shielding);
}*/


/*static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  coap_set_header_content_format(response, TEXT_PLAIN);
  coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "LAST_VALUE %lu\n", (unsigned long) last_value));
}*/

static void
res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  size_t len = 0;
  const char *threshold = NULL;
  const char *action=NULL;
  int success=1;
  const uint8_t *chunk;
 
  len = coap_get_payload(request,&chunk);

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
          //critic value of radiation, and the action on led and shielding are obliged
          leds_on(LEDS_BLUE);
          
          //leds_on(LEDS_GREEN);
          LOG_INFO("start shielding because gas value critic\n");

          printf("status :%d\n",shielding_status);
          if(shielding_status==0){
              LOG_INFO("status is changing\n");
              coap_set_status_code(response, CHANGED_2_04);
              
          }
          shielding_status=1;
          //leds_on(LEDS_YELLOW);
        
    }
    else{
        
          

          //critic value of radiation are not critic, the user can also turn on or turn off the shielding
          if ((action!=NULL && strlen(action)!=0)){
              LOG_DBG("action: %s\n", action);

              // action off 
              if (strncmp(action, "OFF", len) == 0 && shielding_status==1){
                LOG_INFO("stop shielding because user request\n");
                coap_set_status_code(response,CHANGED_2_04);
                leds_off(LEDS_GREEN);
                
                shielding_status=0;
              }
              // action on
              else if (strncmp(action, "ON", len) == 0 && shielding_status==0){
                LOG_INFO("start shielding because user request\n");
                coap_set_status_code(response,CHANGED_2_04);

                leds_on(LEDS_GREEN);
                
                shielding_status=1;
                
              }
              else{
                // action is a string different from off e on, or the action doesn't change the status of the shielding
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