# src/test/resources/features/parallel/parallel_test.feature

@parallel-test
Feature: Parallel Execution Test

  Verify that scenarios run in parallel by observing
  thread IDs and timestamps in console output

  @scenario-1
  Scenario: Parallel Test - Scenario ONE
    When I run a 5 second task for "Scenario ONE"

  @scenario-2
  Scenario: Parallel Test - Scenario TWO
    When I run a 5 second task for "Scenario TWO"

  @scenario-3
  Scenario: Parallel Test - Scenario THREE
    When I run a 5 second task for "Scenario THREE"