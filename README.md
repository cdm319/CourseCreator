# CourseCreator
CourseCreator is a web application written using Java EE technologies that simplifies the creation of Google Course Builder MOOCs through the uploading and conversion of Microsoft PowerPoint (.pptx) presentations into a Google Course Builder data format.

Originally a Master's project for Loughborough University, the source code has now been made open-source in order to allow the community to develop the product further.

## What is a MOOC?
"MOOC" stands for Massive Open Online Course, and is defined by pioneers McAuley et al. as "An online course with the option of free and open registration, a publicly shared curriculum, and open-ended outcomes.  MOOCs integrate social networking, accessible online resources, and are facilitated by leading practitioners in the field of study.  Most significantly, MOOCs build on the engagement of learners who self-organize their participation according to learning goals, prior knowledge and skills, and common interests."

## What is Google Course Builder?
Course Builder is an open source platform that allows the creation of MOOCs.  It runs on the Google App Engine PaaS.  Recent releases have enabled prospective MOOC authors to create content through a web based interface, however there is no current functionality allowing the import of content from PowerPoint presentations (the lecture format of choice for most academics and lecturers) or similar.

## Technology Stack
The application, in its current form, uses the following technologies:
* Java EE
* Apache Tomcat 7
* JavaServer Faces 2.2.7

The application also makes use of a number of third party libraries:
* Apache POI
* Hibernate Validator
* JUnit
* JQuery
* TinyMCE
* LESS CSS
