export const sendMessageToAI = (message, history = []) => {
  if (!message || message.trim() === '') {
    return Promise.reject(new Error("Message cannot be empty"));
  }
  const validHistory = history.filter(item =>
    item && typeof item === 'object' &&
    (item.role === 'user' || item.role === 'model') &&
    Array.isArray(item.parts) && item.parts.length > 0 &&
    typeof item.parts[0].text === 'string'
  );

  return new Promise((resolve) => {
    setTimeout(() => {
      const fakeResponseText = `This is a mocked AI response to: "${message}". History had ${validHistory.length} valid entries.`;
      resolve({ data: { text: fakeResponseText } });
    }, 500 + Math.random() * 500);
  });
};
