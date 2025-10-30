function showForm(type) {
  const area = document.getElementById("form-area");
  const title = document.getElementById("form-title");
  const out = document.getElementById("output");
  out.innerText = "";
  let html = "";

  const formTitle = {
    open: "Open New Account",
    deposit: "Deposit Money",
    withdraw: "Withdraw Funds",
    transfer: "Transfer Funds",
    statement: "Account Statement",
    search: "Search Accounts"
  };
  title.innerText = formTitle[type] || "Secure Bank";

  switch (type) {
    case "open":
      html = `
        <input id="name" placeholder="Customer Name">
        <input id="email" placeholder="Email Address">
        <input id="type" placeholder="Account Type (SAVINGS/CURRENT)">
        <input id="initial" placeholder="Initial Deposit (optional)">
        <button onclick="openAccount()">Create Account</button>`;
      break;

    case "deposit":
      html = `
        <input id="account" placeholder="Account Number">
        <input id="amount" placeholder="Deposit Amount">
        <button onclick="deposit()">Deposit</button>`;
      break;

    case "withdraw":
      html = `
        <input id="account" placeholder="Account Number">
        <input id="amount" placeholder="Withdrawal Amount">
        <button onclick="withdraw()">Withdraw</button>`;
      break;

    case "transfer":
      html = `
        <input id="from" placeholder="From Account">
        <input id="to" placeholder="To Account">
        <input id="amount" placeholder="Amount to Transfer">
        <button onclick="transfer()">Transfer</button>`;
      break;

    case "statement":
      html = `
        <input id="account" placeholder="Account Number">
        <button onclick="statement()">View Statement</button>`;
      break;

    case "search":
      html = `
        <input id="name" placeholder="Customer Name">
        <button onclick="search()">Search</button>`;
      break;
  }

  area.innerHTML = html;
}

// Helper
function val(id) { return document.getElementById(id)?.value.trim() || ""; }
function show(txt) { document.getElementById("output").innerText = txt; }

// Backend calls
async function openAccount() {
  const name = val("name"), email = val("email"), type = val("type"), initial = val("initial");
  const res = await fetch(`/api/open?name=${name}&email=${email}&type=${type}&initial=${initial}`);
  show(await res.text());
}

async function deposit() {
  const acc = val("account"), amt = val("amount");
  const res = await fetch(`/api/deposit?account=${acc}&amount=${amt}`);
  show(await res.text());
}

async function withdraw() {
  const acc = val("account"), amt = val("amount");
  const res = await fetch(`/api/withdraw?account=${acc}&amount=${amt}`);
  show(await res.text());
}

async function transfer() {
  const from = val("from"), to = val("to"), amt = val("amount");
  const res = await fetch(`/api/transfer?from=${from}&to=${to}&amount=${amt}`);
  show(await res.text());
}

async function statement() {
  const acc = val("account");
  const res = await fetch(`/api/statement?account=${acc}`);
  show(await res.text());
}

async function listAccounts() {
  const res = await fetch(`/api/list`);
  show(await res.text());
}

async function search() {
  const q = val("name");
  const res = await fetch(`/api/search?name=${q}`);
  show(await res.text());
}
