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
    Then the response status is 201 "CREATED"
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

  Scenario: Get a single meeting
    Given the database is empty
    And the default meeting exists
    When I GET to the default meeting's ID
    Then I receive a JSON representation of the meeting:
    """
      {
         "id": $meeting.id,
         "date": "2013-02-17",
         "title": "test meeting"
      }
    """

  Scenario: Update Survey Answers
    Given the database is empty
    And the default meeting exists
    When I POST to the default meeting's ID's survey:
    """
    {
      "q1": 0,
      "q2": 0
    }
    """
    Then the response status is 200 "OK"
    And the backend contains a survey response for the default meeting:
      | q1 | q2 |
      | 0  | 0  |

  Scenario: Multiple survey answers
    Given the database is empty
    And the default meeting exists
    When I POST to the default meeting's ID's survey:
    """
    {
      "q1": 2,
      "q2": 2
    }
    """
    Then the response status is 200 "OK"
    When I POST to the default meeting's ID's survey:
    """
    {
      "q1": -2,
      "q2": -2
    }
    """
    Then the response status is 200 "OK"
    And the backend contains survey responses for the default meeting:
      | q1 | q2 |
      | 2  | 2  |
      | -2 | -2 |

  @wip
  Scenario: get Survey Answers for a meeting
    Given the database is empty
    And the default meeting exists
    When I POST some survey results to the default meeting ID:
      | Q1 | Q2 |
      | 2  | 2  |
      | 0  | 0  |
      | -1 | -1 |
    When I GET to the default meeting's survey
    Then I receive a JSON representation of the meeting results:
    """
    [
     {
      "id": 1,
      "meetingId": $meeting.id,
      "answer1": 2,
      "answer2": 2
     },
     {
      "id":2,
      "meetingId": $meeting.id,
      "answer1": 0,
      "answer2": 0
     },
     {
      "id": 3,
      "meetingId": $meeting.id,
      "answer1": -1,
      "answer2": -1
     }
    ]
    """

