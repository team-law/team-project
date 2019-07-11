Group Project - README Template
===

# APP_NAME_HERE

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
[Description of your app]

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:**
- **Mobile:**
- **Story:**
- **Market:**
- **Habit:**
- **Scope:**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* share pictures among multiple people
* limit number of pictures taken
* easy way for people to join groups
* time limit for taking pictures period 
* easy way to access pictures after the event has ended 


**Optional Nice-to-have Stories**

* video/ picture complation after the event that tells the story
* retro filter on picture
* 

### 2. Screen Archetypes

* [list first screen here]
   * [list associated required story here]
   * ...
* [list second screen here]
   * [list associated required story here]
   * ...

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* [fill out your first tab]
* [fill out your second tab]
* [fill out your third tab]

**Flow Navigation** (Screen to Screen)

* [list first screen here]
   * [list screen navigation here]
   * ...
* [list second screen here]
   * [list screen navigation here]
   * ...

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models

User
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | username      | String   | unique user id |
   | password      | String     | secret access key |
   | profilePictue        | File| image displayed in user profile |
   | mediaLinks     | List<Strings>   | links to other social media accounts |
   | friends | List <User>  | friends |

Picture
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | user      | String   | user who created the image |
   | image        | File | picture taken |
   | time         | String     | time at which picture is taken |
   | event         | Event     | event at which picture is taken |
   
UserNode
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | event      | Event   | event at which node is displayed |
   | user        | User| user being displayed |
   | host         | Boolean     | indicator whether or not the node is hosting |
   | parentNode       | UserNode   | who invited the user |
   | picturesCreated | List<Pictures>   | images taken at event |
   
Event
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | timeFrame      | String   | time details |
   | guestList        | List<User> | list of users invited |
   | pictures         | Lists<Picture>     | all pictures taken at event |
   | picsPerPerson       | Integer   | limit of pictures taken at event |
   | location | String   | location of event |
   | description    | String   | summary of the event |
   | title     | String | Name of event |
   | hosts    | List<Users> | users hosting event |

### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
