const functions = require("firebase-functions");
const admin = require("firebase-admin");
const axios = require("axios");
admin.initializeApp();

const GEMINI_KEY = functions.config().gemini?.key;
const GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models";

async function callGemini(model, prompt) {
  const endpoint = `${GEMINI_URL}/${model}:generateContent?key=${GEMINI_KEY}`;
  const payload = {
    temperature: 0.2,
    candidateCount: 1,
    maxOutputTokens: 700,
    prompt: { text: prompt }
  };
  const res = await axios.post(endpoint, payload, {
    headers: { "Content-Type": "application/json" },
    timeout: 120000
  });
  // adapt to response shape:
  return res.data?.candidates?.[0]?.content?.parts?.[0]?.text
    || res.data?.candidates?.[0]?.content?.[0]?.text
    || JSON.stringify(res.data);
}

exports.generateDescription = functions.https.onCall(async (data, context) => {
  const { title } = data;
  if (!title) throw new functions.https.HttpsError("invalid-argument", "title required");
  const prompt = `Write a detailed task description for the title: "${title}". Include objective, deliverables, steps, and estimated time.`;
  const text = await callGemini("gemini-1.5-flash", prompt);
  return { description: text };
});

exports.taskHelper = functions.https.onCall(async (data, context) => {
  const { taskText } = data;
  if (!taskText) throw new functions.https.HttpsError("invalid-argument", "taskText required");
  const prompt = `Explain the following task in simple, actionable steps for an employee:\n\n"${taskText}"`;
  const text = await callGemini("gemini-1.5-flash", prompt);
  return { simplified: text };
});

exports.generateAISummary = functions.https.onCall(async (data, context) => {
  const teamId = data?.teamId;
  const tasksSnap = teamId
    ? await admin.firestore().collection("tasks").where("teamId","==",teamId).get()
    : await admin.firestore().collection("tasks").get();
  const tasks = [];
  tasksSnap.forEach(d => tasks.push(d.data()));
  const prompt = `You are an AI project manager. Summarize these tasks (max 50): ${JSON.stringify(tasks.slice(0,50))}. Provide counts by status and 2 actionable recommendations.`;
  const text = await callGemini("gemini-1.5-flash", prompt);
  return { summary: text };
});
