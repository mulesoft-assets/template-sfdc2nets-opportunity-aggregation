
# Template: Salesforce to Netsuite Opportunity Aggregation

Aggregates opportunities from Salesforce and NetSuite into a CSV file. This basic pattern can be modified to collect from more or different sources and to produce formats other than CSV. You can trigger this manually or programmatically with an HTTP call. 

![b08dc2be-06f5-4254-b51c-43c4e1d4f5b6-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/b08dc2be-06f5-4254-b51c-43c4e1d4f5b6-image.png)

Opportunities are sorted such that the opportunities only in Salesforce appear first, followed by the ones only in NetSuite, and lastly by the ones found in both systems. The custom sort or merge logic can be easily modified to present the data as needed. This template also serves as a base for building APIs using the Anypoint Platform.

# License Agreement

Using this template is subject to the conditions of this [MuleSoft License Agreement](https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf "MuleSoft License Agreement"). Review the terms of the license before downloading and using this template. In short, you are allowed to use the template for free with Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio.

### Use Case

As an administrator I want to aggregate opportunities from Salesforce and NetSuite instances and compare them to see which opportunities can only be found in one of the two and which opportunities are in both instances. 

This template generates the result in the format of an email with an attached CSV Report.

This template is a foundation for extracting data from the two systems, aggregating data, comparing values of fields for the objects, and generating a report of the differences. 

This implementation retrieves opportunities from both Salesforce and Netsuite instances, compares them (opportunities match if the names are equal), and generates a CSV file that shows opportunities in Salesforce, opportunities in NetSuite, and opportunities present in both systems. The report is sent to a configured group of email addresses.

## Salesforce Considerations

For this template to work, be aware of your Salesforce field configuration:

- Where can I check that the field configuration for my Salesforce instance is the right one? [Salesforce: Checking Field Accessibility for a Particular Field](https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US "Salesforce: Checking Field Accessibility for a Particular Field")
- Can I modify the Field Access Settings? How?  [Salesforce: Modifying Field Access Settings](https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US "Salesforce: Modifying Field Access Settings")

### As a Data Source

If the user configured in the template for the source system does not have at least _read only_ permissions for the fields that are fetched, then a _InvalidFieldFault_ API fault appears.

```
java.lang.RuntimeException: [InvalidFieldFault [ApiQueryFault [ApiFault  
exceptionCode='INVALID_FIELD'
exceptionMessage='
Account.Phone, Account.Rating, Account.RecordTypeId, Account.ShippingCity
^
ERROR at Row:1:Column:486
No such column 'RecordTypeId' on entity 'Account'. If you are attempting 
to use a custom field, be sure to append the '__c' after the custom field 
name. Reference your WSDL or the describe call for the appropriate names.'
]
row='1'
column='486'
]
]
```

### Importing the Template into Studio

In Studio, click the Exchange X icon in the upper left of the taskbar, log into your

Anypoint Platform credentials, search for the template, and click **Open**.

### Running on Studio

After opening your template in Anypoint Studio, follow these steps to run it:

- Locate the properties file `mule.dev.properties`, in src/main/resources
- Complete all the properties required as per the examples in the section "Properties to be configured".
- Once that is done, right click your template project folder .
- Hover your mouse over `Run as`.
- Click `Mule Application (configure)`.
- Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
- Click `Run`.

### Running on Mule Runtime Standalone

Complete all properties in one of the property files, for example in mule.prod.properties

and run your app with the corresponding environment variable to use it. To follow the example, this is `mule.env=prod`. 

After this, to trigger the use case, browse to the local HTTP endpoint with the port you configured in your file. For example, if port `9090`, browse to `http://localhost:9090/generatereport` and this creates the CSV report and sends it to the configured e-mail addresses.

## Running on CloudHub

While creating your application on CloudHub (or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in **Properties to Configure** as well as the **mule.env**.

After your app is set up and started, if the domain name is `sfdc2nets-opportunity-aggregation`, to trigger the use case, browse to `http://sfdc2nets-opportunity-aggregation.cloudhub.io/generatereport` which causes the CSV report to be sent by email.

### Deploying Your Template on CloudHub

In Studio, right click your project name in Package Explorer and select

Anypoint Platform > Deploy on CloudHub.

## Properties to Configure

To use this template, configure properties such as credentials, configurations, and more either in a properties file or in CloudHub as Environment Variables. 

### Application Configuration

#### HTTP Connector Configuration

- http.port `9090` 

#### SalesForce Connector Configuration

- sfdc.a.username `PollyHedra@org`
- sfdc.a.password `HedraPassword123`
- sfdc.a.securityToken `avsfwCUl7apQs56Xq2AKi3X`
- sfdc.a.url `https://login.salesforce.com/services/Soap/u/40.0`

#### SMTP Services Configuration

- smtp.host `smtp.gmail.com`
- smtp.port `587`
- smtp.user `exampleuser@gmail.com`
- smtp.password `ExamplePassword456`

#### Mail Details

- mail.from `exampleuser@gmail.com`
- mail.to `woody.guthrie@gmail.com`
- mail.subject `Opportunities Report`
- mail.body `Find attached your Opportunities Report`
- attachment.name `opportunities_report.csv`

#### NetSuite Connector Configuration

- netsuite.email `email@example.com`
- netsuite.password `netsuite_password`
- netsuite.account `YHCtmLGOdrjkKvBruTKaiLan`
- netsuite.roleId `1`
- netsuite.appId `42ABCBD6-AF9F-11E5-BF7F-FEFF819CDC9F`

# API Calls

Salesforce imposes limits on the number of API calls that can be made. However, we make API call to Salesforce only once during aggregation.

# Customize It!

This brief guide provides a high level idea of how this template is built and how you can change it according to your needs.

As Mule applications are based on XML files, this page is organized by describing the XML that conform the template.

More files can be found such as test classes and Mule application files, but to keep it simple the following sections focus on the XML files.

The main XML files in this application are:

- config.xml
- endpoints.xml
- businessLogic.xml
- errorHandling.xml

## config.xml

Configuration for Connectors and Configuration Properties are set in this file. Even you can change the configuration here, all parameters that can be modified here are in properties file, and this is the recommended place to do it so. If you want to do core changes to the logic you probably need to modify this file.

In the visual editor, this file can be found on the _Global Element_ tab.

## endpoints.xml

This is the file where the endpoint starts the aggregation. This template has an HTTP Inbound Endpoint as the way to trigger the use case.

### Trigger Flow

**HTTP Listener** - Start Report Generation

- `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
- The path configured by default is `generatereport` and you are free to change for the one you prefer.
- The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub routes requests from your application domain URL to the endpoint.

## businessLogic.xml

Functional aspect of the template is implemented on this XML, directed by one flow responsible of conducting the aggregation of data, comparing records and finally formating the output, in this case being a report.

Using Scatter-Gather component we are querying the data in different systems. After that the aggregation is implemented in DataWeave 2 script using Transform component.

Aggregated results are sorted by source of existence:

1. Opportunities only in Salesforce
2. Opportunities only in Netsuite
3. Opportunities in both Salesforce and Netsuite

and transformed to CSV format. Final report in CSV format is sent to email, that you configured in mule.\*.properties file.

## errorHandling.xml

This handles how your integration reacts depending on the different exceptions.

This file provides error handling referenced by the main flow in the business logic.

