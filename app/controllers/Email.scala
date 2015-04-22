package controllers

import play.api.mvc._
import com.typesafe.plugin._
import play.api.Play.current

object Email extends Controller {

  def sendEmail() = Action {
    val mail = use[MailerPlugin].email
    //TODO get subject (Question or Purchase)
    mail.setSubject("Purchase book")
    //TODO get the seller's email using id
    mail.setRecipient("Mohamed Camara <mcamara9@gmail.com>","mcamara9@gmail.com")
     //TODO get senderEmail() using his login info
    mail.setFrom("Mohamed Camara <mcamara9@gmail.com>")
    //TODO get buyer's message and send it
   val sending = mail.send( "getBuyerMessage" )
    //TODO display email sent confirmation
    Ok("Confirmation: Message sent")
  }
}
