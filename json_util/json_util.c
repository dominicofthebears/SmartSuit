#include "json_util.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdint.h>


char* get_json_value_string(const char* jsonString, const char* fieldName){

        char* value=NULL;

        //printf("jsonString:%s\n",jsonString);
        //printf("fieldName:%s\n",fieldName);

        char string_json[50];

        strcpy(string_json,jsonString);

        

        for(int i=0;i<strlen(jsonString);i++){
                string_json[i]=string_json[i+1];
        }

        string_json[strlen(string_json)-1]='\0';
        //printf("jsonString:%s\n",string_json);

        //---------------------------------

        char* token = strtok(string_json, ",");
        char first_command[20];

        if(strcmp(fieldName,"action")==0){
        //while (token != NULL) {
                //printf("%s\n", token);
                strcpy(first_command,token);
                //token = strtok(NULL, ",");
        }
        else if(strcmp(fieldName,"threshold")==0){
                while (token != NULL) {
                        //printf("%s\n", token);
                        strcpy(first_command,token);
                        token = strtok(NULL, ",");
                }
        }

        //printf("jsonString:%s\n",first_command);
        char* token2 = strtok(first_command, ":");
        char value_action[10];

        while (token2 != NULL) {
                //printf("%s\n", token2);
                strcpy(value_action,token2);
                token2 = strtok(NULL, ":");
        }
        //printf("value:%s",value_action);

        value = (char*)malloc((strlen(value_action) + 1) * sizeof(char));
        if (value == NULL) {
                printf("Memory allocation failed!\n");
                return NULL;
        }

        strcpy(value,value_action);
        
        return value;
}
