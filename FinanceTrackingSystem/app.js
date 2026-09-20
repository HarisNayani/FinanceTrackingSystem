document.addEventListener('DOMContentLoaded', () => {
    // Seed initial mock dataset matching your backend array structures if empty
    if (!localStorage.getItem('transactions')) {
        const initialSeed = [
            { id: "TXN1", date: "2026-09-01", description: "Monthly Paycheck", category: "Salary", amount: 4500.00, type: "INCOME" },
            { id: "TXN2", date: "2026-09-03", description: "Apartment Rent", category: "Housing", amount: 1200.00, type: "EXPENSE" },
            { id: "TXN3", date: "2026-09-05", description: "Weekly Food Run", category: "Groceries", amount: 154.20, type: "EXPENSE" },
            { id: "TXN4", date: "2026-09-12", description: "Website Design Project", category: "Freelance", amount: 850.00, type: "INCOME" }
        ];
        localStorage.setItem('transactions', JSON.stringify(initialSeed));
    }

    refreshDashboard();

    // Catch UI Form Inputs 
    document.getElementById('transaction-form').addEventListener('submit', (e) => {
        e.preventDefault();

        const transactions = JSON.parse(localStorage.getItem('transactions')) || [];

        // Match your exact Java addTransaction methodology variables
        const newTx = {
            id: "TXN" + (transactions.length + 1),
            date: new Date().toISOString().split('T')[0], // Standard YYYY-MM-DD
            description: document.getElementById('description').value.trim(),
            amount: parseFloat(document.getElementById('amount').value),
            type: document.getElementById('type').value,
            category: document.getElementById('category').value.trim()
        };

        transactions.push(newTx);
        localStorage.setItem('transactions', JSON.stringify(transactions));

        document.getElementById('transaction-form').reset();
        refreshDashboard();
        alert("Success! Transaction committed to local state repository.");
    });
});

function refreshDashboard() {
    const transactions = JSON.parse(localStorage.getItem('transactions')) || [];

    // Emulate your exact Java FinanceManager calculations methodologies loop
    let totalIncome = 0;
    let totalExpenses = 0;

    transactions.forEach(t => {
        if (t.type === "INCOME") totalIncome += t.amount;
        if (t.type === "EXPENSE") totalExpenses += t.amount;
    });

    const netBalance = totalIncome - totalExpenses;

    // Hydrate Layout View Elements
    document.getElementById('total-income').textContent = `$${totalIncome.toFixed(2)}`;
    document.getElementById('total-expense').textContent = `$${totalExpenses.toFixed(2)}`;

    const balanceElement = document.getElementById('total-balance');
    const balanceBox = document.getElementById('balance-status-box');
    const healthBadge = document.getElementById('health-badge');

    balanceElement.textContent = `$${netBalance.toFixed(2)}`;

    if (netBalance >= 0) {
        balanceBox.classList.remove('deficit');
        healthBadge.textContent = 'Healthy Status';
    } else {
        balanceBox.classList.add('deficit');
        healthBadge.textContent = 'Warning Status';
    }

    // Hydrate Ledger Historical Rows
    const container = document.getElementById('transaction-list');
    container.innerHTML = '';

    transactions.forEach(item => {
        const li = document.createElement('li');
        const lowerType = item.type.toLowerCase();
        const prefix = item.type === 'INCOME' ? '+' : '-';

        li.className = `transaction-item ${lowerType}`;
        li.innerHTML = `
            <div class="tx-info">
                <span>${item.description}</span>
                <small>${item.date} • ${item.category} [${item.id}]</small>
            </div>
            <strong style="color: ${item.type === 'INCOME' ? '#16a34a' : '#dc2626'};">
                ${prefix}$${item.amount.toFixed(2)}
            </strong>
        `;
        container.appendChild(li);
    });
}
