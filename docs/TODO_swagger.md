 "/ontologies/{prefix}":

    put:
      summary: adds an RDF document
      consumes:
        - multipart/form-data
      produces: 
        - "application/json"
      operationId: addOntologyByPrefix
      parameters:
        - name: prefix
          in: path
          type: string
          required: true
      responses:
        200:
          description: OK - adds an ontology
          schema:
            type: string
        500:
          description: unexpected error
          schema:
            $ref: '#/definitions/Error'

    delete:
      summary: adds an RDF document
      consumes:
        - multipart/form-data
      produces: 
        - "application/json"
      operationId: deleteOntologyByPrefix
      parameters:
        - name: prefix
          in: path
          type: string
          required: true
      responses:
        200:
          description: OK - deletes an ontology
          schema:
            type: string
        500:
          description: unexpected error
          schema:
            $ref: '#/definitions/Error'
