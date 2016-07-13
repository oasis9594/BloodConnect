<?php require_once("includes/session.php"); ?>
<?php require_once("includes/functions.php"); ?>
<?php
	confirm_logged_in();
?>
<?php
	include_once("includes/header.php");
?>
			<table id="structure">
				<tr>
					<td id="navigation">
						&nbsp;	<!-- non breaking space-->	
					</td>
					<td id="page">
						<h2>Staff menu</h2>
						<p>Welcome to the staff area, <?php echo $_SESSION['user_name']; ?></p>
						<ul>
							<li><a href="content.php">Manage Website Content</a></li>
							<li><a href="new_user.php">Add staff user</a></li>
							<li><a href="logout.php">Logout</a></li>
						</ul>
					</td>
				</tr>
			</table>
<?php include_once("includes/footer.php"); ?>