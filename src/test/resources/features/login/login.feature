# src/test/resources/features/login/login.feature

@login
Feature: User Login

  As a registered user
  I want to log in to the application
  So that I can access my dashboard

  Background:
    Given I am on the login page

  @smoke @priority-high
  Scenario: Successful login with valid credentials
    When I login with username "admin" and password "admin123"
    Then I should be redirected to the dashboard
    And I should see welcome message "Welcome, Admin"

  @regression
  Scenario: Login fails with invalid password
    When I login with username "admin" and password "wrong"
    Then I should see error message "Invalid credentials"

  @regression @data-driven
  Scenario Outline: Login with multiple user roles
    When I login with username "<username>" and password "<password>"
    Then I should see welcome message "<welcomeMsg>"

    Examples:
      | username | password | welcomeMsg       |
      | admin    | admin123 | Welcome, Admin   |
      | manager  | mgr456   | Welcome, Manager |
      | analyst  | anl789   | Welcome, Analyst |