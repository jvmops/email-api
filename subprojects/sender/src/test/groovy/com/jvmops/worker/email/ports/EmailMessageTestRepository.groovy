package com.jvmops.worker.email.ports

import com.jvmops.worker.email.model.EmailMessage
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailMessageTestRepository extends MongoRepository<EmailMessage, ObjectId> {

}