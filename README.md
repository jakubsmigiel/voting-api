# Voting API

A demo voting system in JavaSpringBoot with a PostgreSQL database.

You can use the API to create a new campaign with options to vote for. To cast a vote, create a voter.
A voter can cast only one vote in a campaign and a vote can't be retracted. Any voter can be blocked temporarily.
Campaigns can also be closed through the API, after which no more votes can be cast in it. Closing a
campaign is permanent.

## Setup
A docker-compose configuration that creates a development database called `wybory` 
and an adminer instance at `http://localhost:8081` for easy UI access.

```shell
cd database
docker-compose up -d
```

## Build and Run
The project was built with Maven. To compile and run, use JDK 21:
```shell
JAVA_HOME=/usr/lib/jvm/openjdk21 mvn clean install spring-boot:run
```

## Documentation

### API

There is a Swagger view of the API for convenient testing of this project's API.

#### campaign-controller
- `GET` **/campaigns**  - Get all campaigns
- `GET` **/campaigns/getCampaign** - Get a campaign by ID
- `POST` **/campaigns/addNewCampaign**  - Create a campaign
```json
{
  "name": "Ciasto",
  "options": [
    "Szarlotka",
    "Jabłecznik"
  ]
}
```
- `POST` **/campaigns/closeCampaign** - Closes a campaign
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```




#### voter-controller
- `POST` **/voters/setVoteBlock** - Block or unblock a voter
```json
{
  "id": "bf2678e8-35f7-47bc-ad14-86cbc4a2172e",
  "isBlocked": true
}
```
- `POST` **/voters/addVoter** - Create a new voter and get their id
```json
{
  "name": "Alice"
}
```
- `GET` **/voters** - List all voters
- `GET` **/voters/getVoter** - Get details of a voter by ID

#### voting-controller
- `POST` **/voting/makeVote** - Cast a vote
```json
{
  "campaignUuid": "ba7c915f-e1c1-48a2-9338-fdf756e4be4f",
  "voterUuid": "bf2678e8-35f7-47bc-ad14-86cbc4a2172e",
  "option": "Jabłecznik"
}
```

## Design decisions
- UUID IDs for all objects - no broteforceability, no information leaked (as opposed to incremented `long` IDs).
- Code split into modules - DTOs, database, logic (controllers, services, etc.).
- Logging of every step in every relevant logic branch.
- Secure against race conditions thanks to unique constraints.
- All database references and foreign keys reflected in the code and the database schema.
- Briefly documented Swagger UI.

## Future work
- Better comments - I only had the time for describing the code behavior with logs and the Swagger spec.
- Tests - I focused on making the code reliable and well-structured. Before extending the functionality, tests should be added.
- Performance benchmarking, stress testing.
