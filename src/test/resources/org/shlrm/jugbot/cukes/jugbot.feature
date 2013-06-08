Feature: JSON survey information
  I need to be able to put answers to the two survey questions.
  I need to be able to create meetings, that can be surveyed.
  I need to be able to pull the results of that survey.
  I Need to be able to get a list of meetings, so that I know what meetings are available.

  Scenario: Create a meeting
    Given the database is empty
    When I POST the JSON to "/meetings":
    """
  {
     "date": "2013-05-30",
     "title": "A test meeting"
  }
  """
    Then the response status is 204 "CREATED"
    And the backend contains a meeting:
      | Date       | Title          |
      | 2013-05-30 | A test meeting |


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