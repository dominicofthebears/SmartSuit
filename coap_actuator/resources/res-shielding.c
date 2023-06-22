#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"
#include "os/dev/button-hal.h"

#include <string.h>

#if PLATFORM_HAS_LEDS || LEDS_COUNT

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

//static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
//static void res_event_handler(void);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

/* A simple actuator example, depending on the color query parameter and post variable action, corresponding led is activated or deactivated */
RESOURCE(res_shielding,
         "title=\"shielding:?value=0|1 \" PUT action=<action> ;rt=\"Control\"",
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
  const char *value = NULL;
  const char *action=NULL;
  int success=1;
 

  LOG_DBG("handler\n");

  if((len = coap_get_query_variable(request, "value", &value))) {
    LOG_DBG("value %.*s\n", (int)len, value);
    
    int value_int = atoi(value);
    if(value_int==1){
          //critic value of radiation, and the action on led and shielding are obliged
          leds_on(LEDS_RED);
          leds_off(LEDS_GREEN);
          LOG_INFO("start shielding because gas value critic");
          coap_set_status_code(response, CHANGED_2_04);
          leds_on(LEDS_YELLOW);
        
    }
    else{
        
          leds_on(LEDS_GREEN);
          leds_off(LEDS_RED);
          LOG_INFO("stop shielding because gas value no critic");
          coap_set_status_code(response, CHANGED_2_04);
          leds_off(LEDS_YELLOW);

          //critic value of radiation are not critic, the user can also turn on or turn off the shielding
          if ((len = coap_get_post_variable(request, "action", &action))){
              LOG_DBG("action: %s\n", action);

              // action off 
              if (strncmp(action, "off", len) == 0){
                LOG_INFO("stop shielding because user request");
                coap_set_status_code(response,VALID_2_03);
                leds_off(LEDS_YELLOW);
                
                
              }
              // action on
              else if (strncmp(action, "on", len) == 0){
                LOG_INFO("start shielding because user request");
                coap_set_status_code(response,VALID_2_03);

                leds_on(LEDS_YELLOW);
                
                
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