import tkinter as tk
from tkinter import messagebox
from random import choice

grid_size = 3
current_player = "X"
ai_player = "O"
winner = None
board = []
theme = "Light"
stats = {"X": 0, "O": 0, "Draws": 0}
power_ups = {"undo": False, "shield": False, "swap": False}
ai_level = "Medium"  


def reset_game():
    global board, winner, current_player
    winner = None
    current_player = "X"
    board = [""] * (grid_size * grid_size)
    for button in buttons:
        button.config(text="", bg="white", state="normal")
    label.config(text="Player X's Turn")


def check_winner():
    global winner
    for combo in winning_combinations():
        if all(board[i] == current_player for i in combo):
            for i in combo:
                buttons[i].config(bg="green")
            winner = current_player
            messagebox.showinfo("Game Over", f"Player {current_player} Wins!")
            stats[current_player] += 1
            update_stats()
            return True
    if "" not in board:
        winner = "Draw"
        messagebox.showinfo("Game Over", "It's a Draw!")
        stats["Draws"] += 1
        update_stats()
        return True
    return False


def winning_combinations():
    combos = []
    for i in range(grid_size):
        combos.append([i * grid_size + j for j in range(grid_size)])  
        combos.append([i + j * grid_size for j in range(grid_size)])  
    combos.append([i * (grid_size + 1) for i in range(grid_size)])  
    combos.append([(i + 1) * (grid_size - 1) for i in range(grid_size)])  
    return combos



def update_stats():
    stats_label.config(
        text=f"X Wins: {stats['X']} | O Wins: {stats['O']} | Draws: {stats['Draws']}"
    )


def button_click(index):
    global current_player, board
    if not board[index] and not winner:
        board[index] = current_player
        buttons[index].config(text=current_player, bg="cyan" if current_player == "X" else "pink")
        if not check_winner():
            toggle_player()
            if current_player == ai_player:
                ai_move()


def ai_move():
    global board
    empty_indices = [i for i, cell in enumerate(board) if not cell]
    if ai_level == "Easy":
        move = choice(empty_indices)
    elif ai_level == "Medium":
        move = choice(empty_indices) if choice([True, False]) else get_best_move()
    else:  
        move = get_best_move()
    board[move] = ai_player
    buttons[move].config(text=ai_player, bg="pink")
    check_winner()
    toggle_player()


def get_best_move():
    empty_indices = [i for i, cell in enumerate(board) if not cell]
    return choice(empty_indices)  


def toggle_player():
    global current_player
    current_player = "O" if current_player == "X" else "X"
    label.config(text=f"Player {current_player}'s Turn")


def build_grid():
    global buttons, board
    buttons = []
    board = [""] * (grid_size * grid_size)
    for i in range(grid_size):
        for j in range(grid_size):
            btn = tk.Button(
                grid_frame,
                text="",
                font=("Helvetica", 20),
                width=4,
                height=2,
                bg="white",
                command=lambda idx=i * grid_size + j: button_click(idx),
            )
            btn.grid(row=i, column=j)
            buttons.append(btn)


def change_grid_size(size):
    global grid_size
    grid_size = size
    for widget in grid_frame.winfo_children():
        widget.destroy()
    build_grid()
    reset_game()


def apply_theme(selected_theme):
    global theme
    theme = selected_theme
    bg_color, fg_color = ("black", "white") if theme == "Dark" else ("white", "black")
    root.config(bg=bg_color)
    label.config(bg=bg_color, fg=fg_color)
    stats_label.config(bg=bg_color, fg=fg_color)
    for button in buttons:
        button.config(bg="white", fg="black")


root = tk.Tk()
root.title("Ultimate Tic-Tac-Toe")
root.geometry("600x700")

label = tk.Label(root, text="Player X's Turn", font=("Helvetica", 16), bg="white")
label.pack(pady=10)

grid_frame = tk.Frame(root)
grid_frame.pack()

build_grid()

stats_label = tk.Label(root, text="X Wins: 0 | O Wins: 0 | Draws: 0", font=("Helvetica", 12), bg="white")
stats_label.pack(pady=10)

control_frame = tk.Frame(root)
control_frame.pack(pady=10)

reset_button = tk.Button(control_frame, text="Reset", command=reset_game, font=("Helvetica", 12))
reset_button.grid(row=0, column=0, padx=5)

grid3x3_button = tk.Button(control_frame, text="3x3 Grid", command=lambda: change_grid_size(3), font=("Helvetica", 12))
grid3x3_button.grid(row=0, column=1, padx=5)

grid4x4_button = tk.Button(control_frame, text="4x4 Grid", command=lambda: change_grid_size(4), font=("Helvetica", 12))
grid4x4_button.grid(row=0, column=2, padx=5)

theme_button = tk.Button(control_frame, text="Switch Theme", command=lambda: apply_theme("Dark" if theme == "Light" else "Light"), font=("Helvetica", 12))
theme_button.grid(row=0, column=3, padx=5)

root.mainloop()
