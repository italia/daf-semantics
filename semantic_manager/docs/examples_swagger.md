
paths: 

  "/ontonethub/find":  
    post:
        summary: find by term
        produces:
          - application/json
        consumes:
          - application/x-www-form-urlencoded
        operationId: findByTerm
        parameters:
          - in: formData
            name: endpoint
            default: "http://localhost:8000/stanbol/ontonethub/find"
            type: string
            required: true
            description: the endpoint to call
          - in: formData
            name: name
            type: string
            required: true
            description: the term used in query
          - in: formData
            name: lang
            type: string
            required: false
            description: the language used in query
        responses:
          200:
            description: OK
            schema:
              type: string
          500:
            description: unexpected error
            schema:
              $ref: '#/definitions/Error'
        tags: [ontologies]

