# User Stories

## Purpose of this file

This file is intended to provide user stories as part of a modified XP approach to this project. It will be modified into tasks and sub-tasks that will be created through GitHub's issue tracker.

## System metaphor

### A web application that uses a map to show interesting things around the Dyfi Wildlife Centre

## Epics

1. As a volunteer, I want to have a web interface when I can show people information about the nature reserve on a map.
2. As an administrator, I want to be able to add an edit information about the nature reserve, and change anything I want to change.

## Stories

1. As a volunteer, I want to be able to view points of interest on a map, so that I'll be able to tell visitors what is around the centre.
2. As an administrator, I want to be able to login to an authenticated administration panel, so that I can ensure that only authorised people can change information about the points of interest.
3. As an administrator, I want to be able to add and edit information about points of interest, so that I can ensure that there is up-to-date information about the wildlife centre.
4. As a volunteer, I want the ability to showcase the information in both the medium of English and of Welsh, so that I can accommodate for all people who visit the centre, regardless of their preferred language.
5. As an administrator, I want to be able to add images to different points of interest, so that I'll be able to show a visual representation of the point of interest.
6. As a visitor, I want to be able to find directions to and from the centre using the application, so that I'll be able to find out how to get back home, and find out how to get to the centre by another form of transport if I want to visit again.
7. As a visitor, I want to be able to find out about local businesses that are featured in the application, so that I can get more a feel for what is around the wildlife centre, and potentially visit some interesting businesses.
8. As a visitor, I want to be able to be told about what the wildlife centre is doing, so that I can get more of a feel and be able to appreciate the area around me.
9. As a visitor, I want to be able to look at webcams of the Ospreys that live at the wildlife centre.
10. As a volunteer, I want to be able to filter the map by different defining features, and also press a button in case I go too far from the centre.

## Technology stack

- **Database:** PostgreSQL, it is easy to install and I am knowledgeable in it thanks to CS27020.
- **Backend:** A RESTful API coded in the Spring Boot framework, or another similar Java API if necessary. Negates the need to learn a new language and it is a useful skill to have.
- **Frontend:** An extension of the RESTful API in the Spring Boot framework, utilising the Thymeleaf template engine. JavaScript will have to be used to a lesser extenet as the Google Maps API is in JavaScript.
- **CSS framework:** This is more flexible as it is not hard to change the framework. Currently Fomantic UI is a good contender, but Bootstrap may also work and could be simpler. Other options include Foundation and Material Design. All have comparable components.

## Priority

### Legend
Using MoSCoW analysis.

- **M: Must** - Non-negotiable, must be satisfied for the project to be considered a success.
- **S: Should have** - High priority, a critical requirement that should be included if at all possible. However, it can be implemented in other ways if necessary.
- **C: Could have** - A desirable requirement but not necessary, will be included if there's enough time.
- **W: Won't** - A requirement identified as not necessary to be implemented as this stage in planning, but may be considered during future stages.

### User Story priorities

1. **M** - This is the whole point of the application.
2. **S** - Could potentially be implemented as a basic edit interface without authentication, but this would lead to a lot of problems if the centre chooses to publish the app outside an intranet.
3. **M** - The application must be maintainable as I will not be available to assist with updating information after completion of this project.
4. **C** - The Welsh language policy of the trust is not particularly at the forefront of my mind, and could be implemented by just pasting the Welsh below the English. However, it would be nice to involve I18N internationalisation when developing the application.
5. **C** - This is probably easy to implement, but the POI cards could just be text if any problems arise.
6. **C** - Again this is probably easy to implement through pointing out places like the bus stop and the train station, however due to the remote location most visitors would arrive by car anyway.
7. **S** - This is something that will be critical to the application being useful. It can be implemented through Google's geocoding API and through information from the volunteers.
8. **M** - Again, this is the whole point of the application.
9. **W** - When visiting the Dyfi Wildlife Centre, the project manager explained that they will have four TV screens and all but one will show a live feed of the webcam. This is an unnecessary and potentially time-consuming requirement that will only be considered if all other requirements have been achieved.
10. **S** - Filters should be able to be defined on each POI. A reset button that makes a function call to reset the app would be useful, but is not necessarily crucial as pressing refresh would also work given the spike work.

Priority = {1, 3, 8, 2, 10, 7, 4, 5, 6, 9}

## Tasks  

Tasks will be stored as GitHub issues which will be broken down into further subtasks. Each subtask will have its own branch that will merge onto the task branch. Tasks will be carried out sequentially. Each story has a task attached to it, but it is broken down and interpreted in a software engineering sense.

1. Create an interface that includes a map, which has markers that can be added to it. The markers should link to a popup containing information about the point of interest. At this stage, the POIs can be hard-coded.
2. Create an API that allows for information about points of interest to be stored in a database. Program this API to automatically update the Google Maps API with markers and create a page to allow for this to be edited in a textbox and posted to the database. Focus on not having to 'hack' through the HTML or database, similar to a CMS. Worst case: use something like WordPress headless.
3. Re-visit the interface created in task 1 to ensure that the information popup is easy to present to someone. Ensure that the interface is easily-readable, for example by having marker clusters. Consider performing user surveys and informal user testing with friends, and even the customer if possible.
4. Re-visit the interface created in task 2 and use a simple authentication algorithm, such as OAuth 2.0. This should include some sort of user/password table and the ability to create new users. It does not necessarily have to have enterprise-grade encryption but should be taken seriously enough to disallow any simple attacks on the web server.
5. Create a list of filters that can be selected by the volunteer on the main screen. This will filter out some markers based upon specific boolean values. Will require some further spike work through online tutorials.
6. In the admin panel, allow for addresses to be entered and geocoded into coordinates, so that local businesses and places such as educational institutions can be added.
7. Enable I18N internationalisation, creating a basic Welsh version of the web page. Ensure that the admin panel accepts name and description information in both English and Welsh. Don't worry about the accuracy of Welsh as this can be corrected by the customer or by Welsh-speaking staff in the University.
8. Include the ability to upload images to the web server, or, failing that, hotlink images. Have them show up with each POI. A card component could be useful for this.
9. Ensure that forms of public transport are clearly marked on the map, if they haven't been already. Potentially import the directions API and allow visitors to ask the volunteer for driving and public transport directions back to wherever they live to be displayed on the screen.
  - Potential task to implement bus and train times through an open source transport tims API. Examples include the [NextBuses API](https://data.gov.uk/dataset/15be9dba-392c-4375-a931-425e35b956f7/nextbuses-api). [Transport API](https://www.transportapi.com/) and [Darwin](https://www.nationalrail.co.uk/100296.aspx), but this is of C priority.
10. Get the links for video feeds for the customer and implement them as buttons that open up an HTML5 video component.
