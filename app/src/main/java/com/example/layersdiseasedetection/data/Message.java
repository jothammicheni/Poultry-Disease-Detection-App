package com.example.layersdiseasedetection.data;


    public class Message {
        public void setRecipientname(String recipientname) {
            this.recipientname = recipientname;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        private String message;

        private String name;
        private String sender;
        private String recipientname;
        private String timestamp;  // Added timestamp field
        public Message() {
            // Default constructor required for Firebase
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Message(String message, String sender, String recipientname, String timestamp, String name) {
            this.message = message;
            this.sender = sender;
            this.recipientname = recipientname;
            this.timestamp = timestamp;
            this.name= name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getRecipientname() {
            return recipientname;
        }
    }


