package ar.sgt.android.smsscheduler.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.StringTokenizer;

import ar.sgt.android.smsscheduler.widget.RecipientsAutoCompleteTextView;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -740014312764959427L;
	
	private Long id;
	private String contactId;
	private String message;
	private Date sendDate;
	private Integer status;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public StringTokenizer recipients() {
		return new StringTokenizer(Character.toString(RecipientsAutoCompleteTextView.TOKEN));
	}
	
	@Override
	public String toString() {
		return contactId;
	}
	
}
