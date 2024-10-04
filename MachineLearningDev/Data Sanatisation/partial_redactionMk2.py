###MK2 Refined with the adjusted redaction option DO NOT ALTER

#Using regex since we are working with emails, similar string. Common characters like (@)
#Using pandas for the dataframe, cos we need seperate analysing data in excel
import re
import pandas as pd

#path to CSV which contains email addresses (input) and Output is blank for the redacted emails to be processed
#Input + Output seperated to outline the distinct pathways
input_path_csv = r'C:\Users\ranat\email_intest.csv'
output_path_csv = r'C:\Users\ranat\email_outtest.csv'

#particular function that can identify characteristics of email address, and redacts the input from csv file
def redaction_e_address(text):
    email_regex = r'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}'


    ##using split fucntion to seperate the user and domain, and partially redact. 
    #partially redacts the input obtained from the excel sheet
    #Split seperates the domain and user
    def redact_entry(match):
        entry = match.group(0)
        user, domain = entry.split('@')
        domain_parts = domain.split('.')

        #returns the first letter of the user, the first letter of the domain as well as the top level of the domain
        return f"{user[0]}***@{domain_parts[0][0]}***.{'.'.join(domain_parts[1:])}"

#provides the redacted text once joined
    redacted_text = re.sub(email_regex, redact_entry, text)
    return redacted_text

#Fucntion to process and place final redaction output into excel
def process_and_redact_csv(input_path_csv, output_path_csv):
    dataframe = pd.read_csv(input_path_csv)
    print("Original DataFrame:")
    print(dataframe.head())  

#double checks if input in cell is string, and if it is compatible  
    
    def redact_field(field):
        if isinstance(field, str):
            return redaction_e_address(field)
        return field

##dataframe additionally works in conjunction with the redact field

#Using redact field function allows us to redact the input given
    redacted_dataframe = dataframe.applymap(redact_field)
    print("Redacted DataFrame:")
    print(redacted_dataframe.head())  

    redacted_dataframe.to_csv(output_path_csv, index=False)
    

    #confirms anonymisation, snd provides the output as to where the data is listed
    print("Anonymization process completed:", output_path_csv)

#completes the redaction process, and provides output
process_and_redact_csv(input_path_csv, output_path_csv)
