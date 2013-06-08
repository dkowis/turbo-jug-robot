Feature: JSON survey information
  I need to be able to put answers to the two survey questions.
  I need to be able to create meetings, that can be surveyed.
  I need to be able to pull the results of that survey.
  I Need to be able to get a list of meetings, so that I know what meetings are available.

  TODO: the server needs to be running!!!


  Scenario: Create a meeting
    When I POST the JSON to "/meetings":
    """
  {
     "meeting": {
         "date": "2013-05-30 17:33:54 -0500",
         "title": "A test meeting"
     }
  }
  """
    Then the result should be 200 OK


  Scenario: Get a list of meetings
    Given the database is empty
    Given the default meeting exists
    When I GET to "/meetings"
    Then I recieve a list containing my meeting:
    """
  [
    {
        "id": $meeting.id,
        "date": "2013-02-17",
        "title": "test meeting"
    }
  ]
  """