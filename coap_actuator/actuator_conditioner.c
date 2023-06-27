#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"
#include "dev/leds.h"
#include "os/dev/button-hal.h"
#include "coap-blocking-api.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

// Server IP and resource path
#define SERVER_EP "coap://[fd00::1]:5683"
static char *service_url = "/registration";
static coap_endpoint_t server_ep;
static coap_message_t request[1]; 
static bool registered=false;

void client_chunk_handler(coap_message_t *response)
{
        const uint8_t *chunk;
        if(response == NULL) {
            LOG_ERR("Request timed out");
            return;
        }
        LOG_INFO("Registration successful\n");
        int len = coap_get_payload(response, &chunk);
        printf("|%.*s", len, (char *)chunk);
        registered=true;
}

extern coap_resource_t  res_gas; 

static char led_on_color[10] = "yellow";


PROCESS(conditioner_thread, "conditioner");
AUTOSTART_PROCESSES(&conditioner_thread);

PROCESS_THREAD(conditioner_thread, ev, data)
{
  PROCESS_BEGIN();

  //PROCESS_PAUSE();

    while(!registered){
        // REGISTRATION--------------------------------------
        // Populate the coap_endpoint_t data structure
        coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
        // Prepare the message
        coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
        coap_set_header_uri_path(request, service_url);
        // Set the payload 
        const char msg[] = "{\"type\":\"gas\"}";
        coap_set_payload(request, (uint8_t *)msg, sizeof(msg) - 1);

        COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);

        // END REGISTRATION -------------------------------------------
    }

  LOG_INFO("Starting actuator against gas\n");

  coap_activate_resource(&res_gas, "gas");

  static struct etimer e_timer;
  
  etimer_set(&e_timer, CLOCK_SECOND * 2);
  
  printf("ciao\n");

  printf("%s\n",led_on_color);

  //leds_on(LEDS_GREEN);

  button_hal_button_t *btn; 
	
  btn = button_hal_get_by_index(0);
  printf("Device button count: %u.\n", button_hal_button_count);
  if(btn) { 
		printf("%s on pin %u with ID=0, Logic=%s, Pull=%s\n",
		BUTTON_HAL_GET_DESCRIPTION(btn), btn->pin,
		btn->negative_logic ? "Negative" : "Positive",
		btn->pull == GPIO_HAL_PIN_CFG_PULL_UP ? "Pull Up" : "Pull Down");
  }

  while(1) {
       //PROCESS_WAIT_EVENT();
	
    //if(ev == PROCESS_EVENT_TIMER && data == &e_timer){
        //printf("Event triggered\n");
         // Wait until the timer expires or an event occurs
        PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&e_timer) || ev==button_hal_press_event);
        //res_conditioner.trigger();
        
        if(ev == button_hal_press_event){  
            //the red led is blinking and the button of the sensor is pressed
            btn = (button_hal_button_t *)data;
			printf("Press event");
			if (strcmp(led_on_color, "red") == 0) {
                printf("Button pressed while LED is red\n");
                
                
                leds_off(LEDS_RED);
                strcpy(led_on_color,"yellow");
            } 

        }
        else{
            if (leds_get() & LEDS_RED) {
                //printf("LEDS_RED is on\n");
                strcpy(led_on_color,"red");
            } else {
                    //printf("LEDS_RED is off\n");
            }

            if (leds_get() & LEDS_YELLOW) {
                    //printf("LEDS_GREEN is on\n");
                    //situazione non critica smette di lampeggiare di rosso
                    strcpy(led_on_color,"yellow");
            } else {
                    //printf("LEDS_GREEN is off\n");
            }
            //printf("led on is:%s\n",led_on_color);

            //printf("size %zu %zu\n",strlen(led_on_color),strlen("red"));
            

            if(strcmp(led_on_color,"red")==0){
                //printf("rosssoooo");
                leds_toggle(LEDS_RED);
                //leds_off(LEDS_GREEN);
            }
            else if(strcmp(led_on_color,"yellow")==0){
                //printf("greeeeeeen");
                //leds_toggle(LEDS_GREEN);
                leds_off(LEDS_YELLOW);
            }

            

            
            etimer_set(&e_timer, CLOCK_SECOND * 2);

        }

  }                             

  PROCESS_END();
}