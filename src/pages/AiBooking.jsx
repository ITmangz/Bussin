import { useEffect, useRef, useState } from "react";
import {
  Bot,
  CheckCircle2,
  MessageCircle,
  RotateCcw,
  Send,
  User,
} from "lucide-react";

import AppButton from "../components/ui/AppButton";
import { sendAIMessage } from "../services/aiService";
import "./AiBooking.css";

const initialMessages = [
  {
    id: 1,
    role: "assistant",
    text: "Hello! I'm BUSSIN AI. I can help you find and book a bus trip.",
  },
  {
    id: 2,
    role: "assistant",
    text: "Tell me where you're traveling from, where you're going, and your preferred travel date.",
  },
];

const suggestedPrompts = [
  "I want to book a trip",
  "Find available trips",
  "Show my bookings",
];

function AIBooking() {
  const [messages, setMessages] = useState(initialMessages);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const messagesEndRef = useRef(null);
  const textareaRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages, isLoading]);

  async function sendMessage(message = input) {
    const trimmedMessage = message.trim();

    if (!trimmedMessage || isLoading) {
      return;
    }

    const userMessage = {
      id: Date.now(),
      role: "user",
      text: trimmedMessage,
    };

    setMessages((current) => [...current, userMessage]);

    setInput("");
    setIsLoading(true);

    try {
      console.log("Sending AI message:", trimmedMessage);

      const response = await sendAIMessage(trimmedMessage);

      console.log("AI response:", response);

      const assistantMessage = {
        id: Date.now() + 1,
        role: "assistant",
        text:
          response?.message ||
          response?.response ||
          response?.reply ||
          "I received your message.",
      };

      setMessages((current) => [...current, assistantMessage]);
    } catch (error) {
      console.error("AI booking request failed:", error);

      let errorMessage =
        "Sorry, I couldn't connect to BUSSIN AI. Please try again.";

      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.data?.error) {
        errorMessage = error.response.data.error;
      } else if (error.message) {
        console.error("Request error:", error.message);
      }

      setMessages((current) => [
        ...current,
        {
          id: Date.now() + 1,
          role: "assistant",
          text: errorMessage,
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  }

  function handleKeyDown(event) {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      sendMessage();
    }
  }

  function handlePromptClick(prompt) {
    sendMessage(prompt);
  }

  function clearConversation() {
    setMessages(initialMessages);
    setInput("");
    setIsLoading(false);
  }

  return (
    <section className="ai-booking-page">
      <header className="ai-booking-header">
        <div>
          <div className="ai-booking-title-row">
            <div className="ai-booking-title-icon">
              <MessageCircle size={21} />
            </div>

            <h1>AI Booking</h1>
          </div>

          <p>Book your bus trip using natural language.</p>
        </div>

        <AppButton
          variant="secondary"
          onClick={clearConversation}
          className="ai-clear-button"
        >
          <RotateCcw size={15} />
          New Conversation
        </AppButton>
      </header>

      <div className="ai-booking-container">
        <div className="ai-chat-card">
          <div className="ai-chat-header">
            <div className="ai-chat-brand">
              <div className="ai-chat-avatar">
                <Bot size={20} />
              </div>

              <div>
                <strong>BUSSIN AI</strong>

                <span>
                  <span className="ai-online-dot" />
                  Online
                </span>
              </div>
            </div>

            <div className="ai-chat-status">AI Assistant</div>
          </div>

          <div className="ai-messages">
            <div className="ai-welcome">
              <div className="ai-welcome-icon">
                <Bot size={25} />
              </div>

              <h2>How can I help?</h2>

              <p>
                I can help you find trips, select seats, and complete your
                booking.
              </p>
            </div>

            {messages.map((message) => (
              <div
                key={message.id}
                className={`ai-message-row ${message.role}`}
              >
                {message.role === "assistant" && (
                  <div className="message-avatar assistant-avatar">
                    <Bot size={15} />
                  </div>
                )}

                <div className="ai-message-content">
                  <div className="ai-message-name">
                    {message.role === "assistant" ? "BUSSIN AI" : "You"}
                  </div>

                  <div className="ai-message-bubble">{message.text}</div>
                </div>

                {message.role === "user" && (
                  <div className="message-avatar user-avatar">
                    <User size={15} />
                  </div>
                )}
              </div>
            ))}

            {isLoading && (
              <div className="ai-message-row assistant">
                <div className="message-avatar assistant-avatar">
                  <Bot size={15} />
                </div>

                <div className="ai-message-content">
                  <div className="ai-message-name">BUSSIN AI</div>

                  <div className="ai-message-bubble typing-bubble">
                    <span />
                    <span />
                    <span />
                  </div>
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          <div className="ai-suggestions">
            <span>Suggested</span>

            <div className="suggestion-list">
              {suggestedPrompts.map((prompt) => (
                <button
                  key={prompt}
                  type="button"
                  onClick={() => handlePromptClick(prompt)}
                  disabled={isLoading}
                >
                  {prompt}
                </button>
              ))}
            </div>
          </div>

          <div className="ai-input-area">
            <textarea
              ref={textareaRef}
              value={input}
              onChange={(event) => setInput(event.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Type your message..."
              rows={1}
              disabled={isLoading}
            />

            <button
              type="button"
              className="ai-send-button"
              onClick={() => sendMessage()}
              disabled={!input.trim() || isLoading}
              aria-label="Send message"
            >
              <Send size={18} />
            </button>
          </div>

          <div className="ai-disclaimer">
            <CheckCircle2 size={13} />
            BUSSIN AI helps you book trips through the BUSSIN system.
          </div>
        </div>
      </div>
    </section>
  );
}

export default AIBooking;
