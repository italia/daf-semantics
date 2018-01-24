    get:
      summary: list of ontologies
      consumes:
        - multipart/form-data
      operationId: listOntologies
      responses:
        200:
          description: OK
          schema:
            $ref: '#/definitions/ResultMessage'
        500:
          description: unexpected error
          schema:
            $ref: '#/definitions/Error'
      tags: [ontologies]