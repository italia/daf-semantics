
  

  //  def extract_parents_relations(vbox: VocabularyBox) = {
  //    SPARQL(vbox.repo).query(q_instances)
  //      .map { item =>
  //        val instance_uri = item.getOrElse("instance_uri", "").toString()
  //        val parent_uri = item.getOrElse("parent_uri", "").toString()
  //        ElementWithParent(instance_uri, parent_uri)
  //      }
  //  }

  /*
   * TODO: move this method directly in the VBox, if possible!
   */
  //  def extract_instances(vbox: VocabularyBox): Seq[String] = {
  //
  //    SPARQL(vbox.repo).query(q_instances)
  //      .map(_.getOrElse("instance_uri", "").toString())
  //      .filterNot(_.trim().equals(""))
  //
  //  }

  //  def standardize(vbox: VocabularyBox) = {
  //
  //    val hierarchies = extract_hierarchies(vbox)
  //
  //    println(hierarchies.toList)
  //
  //    hierarchies.map { element =>
  //
  //      val q_details = this.q_details(element.uri)
  //
  //      val _ontoID = vbox.extract_assetType()._1
  //      val _vocID = vbox.id
  //
  //      val results = SPARQL(vbox.repo).query(q_details).toList(0).toMap
  //
  //      // TODO: collaps multiple values for the same property as an array!
  //      val instanceID = results.getOrElse("instance_uri", "").toString().replaceAll(".*[#/](.*)", "$1")
  //      val conceptID = results.getOrElse("concept_uri", "").toString().replaceAll(".*[#/](.*)", "$1")
  //      val propertyID = results.getOrElse("property_uri", "").toString().replaceAll(".*[#/](.*)", "$1")
  //
  //      val doc = results ++ Map(
  //        "instanceID" -> instanceID,
  //        "conceptID" -> conceptID,
  //        "propertyID" -> propertyID,
  //        "hierarchy" -> element.path,
  //        "ontologyID" -> _ontoID,
  //        "vocabularyID" -> _vocID)
  //
  //      doc
  //
  //    }
  //
  //  }

  //  def toCSV(vbox: VocabularyBox) = {
  //
  //    val hierarchies = extract_hierarchies(vbox).toList
  //
  //    //    val MAX_SIZE = hierarchies.map(_.path.size).max
  //    //    println("MAX_SIZE: " + MAX_SIZE)
  //
  //    // TODO: expand details
  //    // TODO: query parametrization
  //    // TODO: SPARQL fluent construction
  //    // TODO: fill empty cells
  //
  //    // fetch of instances details...
  //    val _instances = hierarchies.map { h =>
  //      val uri = h.uri
  //      val _details = SPARQL(vbox.repo).query(q_details(uri))(0)
  //      (uri, _details)
  //    }
  //
  //    val _cells = hierarchies.map { h =>
  //
  //      val uri = h.uri
  //      val path = h.path
  //      val test = hierarchies.filter(_.path.containsSlice(path)).maxBy(h => h.path.size)
  //
  //      test
  //    }.distinct
  //      .map { el =>
  //
  //        val uri = el.uri
  //        val path = el.path
  //
  //        val id = uri.replaceAll(".*[#/](.*)", "$1")
  //        val ids = el.path.map(_.replaceAll(".*[#/](.*)", "$1")).toList
  //        (id, ids)
  //
  //        // elements details lookup
  //        path.map { uri =>
  //          _instances.find(el => uri.equals(el._1)).head._2
  //        }.toList
  //
  //      }
  //
  //    _cells
  //
  //  }

