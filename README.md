# Spring REST API
___
Projekt REST API utworzony w Springu. Korzysta z bazy danych H2. Posiada podstawowy system uwierzytelniania przy próbie dostępu do zasobów.
___
Opis endpointów:
<br/><br/>
Projekty
* GET /api/projects - zwraca listę wszystkich projektów zapisanych w bazie danych (możliwość podania opcji stronicowania)
* GET /api/projects/{projectId} - zwraca dane projektu o ID przesłanym w żądaniu
* GET /api/projects?name={projectName} - zwraca listę projektów zawierających w nazwie podany w parametrze name ciąg znaków (możliwość podania opcji stronicowania)
* POST /api/projects - tworzy w bazie danych nowy projekt na podstawie danych przesłanych w ciele żądania, zwraca adres do utworzonego zasobu. Przykład ciała żądania:
  ```
  {
      "name": "TestProject",
      "description": "Test Description",
      "handinDate": "2021-05-29"
  }
  ```
* PUT /api/projects/{projectId} - aktualizuje projekt o podanym w żądaniu ID danymi przesłanymi w ciele żądania
* PUT /api/projects/{projectId}/tasks/{taskId} - przypisuje zadanie o wskazanym ID (taskId) do projektu o podanym ID (projectId)
* DELETE /api/projects/{projectId} - usuwa z bazy danych projekt o wskazanym ID

Zadania
* GET /api/tasks - zwraca listę wszystkich zadań zapisanych w bazie danych (możliwość podania opcji stronicowania)
* GET /api/tasks/{taskId} - zwraca dane zadania o ID przesłanym w żądaniu
* GET /api/tasks?projectId={projectId} - zwraca listę zadań przypisanych do projektu o ID podanym w parametrze projectId (możliwość podania opcji stronicowania)
* POST /api/tasks - tworzy w bazie danych nowe zadanie na podstawie danych przesłanych w ciele żądania, zwraca adres do utworzonego zasobu. Przykład ciała żądania:
  ```
  {
      "name": "TestTask",
      "numberInSequence": 1,
      "description": "Test Description"
  }
  ```
* PUT /api/tasks/{taskId} - aktualizuje zadanie o podanym w żądaniu ID danymi przesłanymi w ciele żądania
* DELETE /api/tasks/{taskId} - usuwa z bazy danych zadanie o wskazanym ID
