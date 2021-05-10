import React from 'react';
import firebase from 'firebase/app';
import "firebase/database";
import "firebase/storage";
import 'materialize-css/dist/css/materialize.min.css';
import './App.css';

let firebaseConfig = {
	apiKey: "AIzaSyDx0VKIQAe5JgskQ3qEqr9mHikCLQARA-A",
	authDomain: "don-t-starve-4a89a.firebaseapp.com",
	databaseURL: "https://don-t-starve-4a89a-default-rtdb.firebaseio.com",
	projectId: "don-t-starve-4a89a",
	storageBucket: "don-t-starve-4a89a.appspot.com",
	messagingSenderId: "230573217947",
	appId: "1:230573217947:web:884b9b75298c4584bb0e3e",
	measurementId: "G-86FH84N9ZG"
}

if (!firebase.apps.length) firebase.initializeApp(firebaseConfig)

class App extends React.Component {
	constructor(props) {
		super(props)
		this.state = {
			isMenu: true,
			vData: [],
			breakfast: [],
			lunch: [],
			snacks: [],
			dinner: [],
		}
		this.renderMenu = this.renderMenu.bind(this)
		this.renderVerify = this.renderVerify.bind(this)
		this.db = firebase.database()
		this.storage = firebase.storage().ref('/Photos');
		this.vSet = new Set([])
		this.handleSubmit2 = this.handleSubmit2.bind(this)
	}
	componentDidMount() {
		this.db.ref('/users').on('value', snapshot => {
			let data = Object.values(snapshot.val())
			data = data.filter(i => !i.verified)
			data.forEach(i => {
				let x = this.storage.child(i.regno)
				x.getDownloadURL().then(j => {i.img_url = j;this.forceUpdate()})
			})
			this.setState({ vData: data })
		})
		this.db.ref('/menu').on('value', snapshot => {
			const data = Object.values(snapshot.val())
			this.setState({
				breakfast: data[0],
				lunch: data[1],
				snacks: data[2],
				dinner: data[3]
			})
		})
	}
	handleSubmit(e) {
		e.preventDefault()
		let menu = {
			breakfast: this.state.breakfast,
			lunch: this.state.lunch,
			snacks: this.state.snacks,
			dinner: this.state.dinner
		}
		this.db.ref('/menu').set(menu)
		console.log(this.state)
	}
	handleSubmit2(e) {
		e.preventDefault()
		let data = this.state.vData
		let v = {}
		for(let i=0;i<data.length;i++){
			if(this.vSet.has(data[i].regno)) data[i].verified = true
			else data[i].verified = false
			v[data[i].regno] = data[i]
		}
		this.db.ref('/users').update(v)
		this.forceUpdate()
	}
	handleCheck(e) {
		let check = e.target.checked
		let reg = e.target.parentNode.parentNode.nextSibling.nextSibling.nextSibling.innerText
		if(check) this.vSet.add(reg)
		else this.vSet.delete(reg)
	}
	renderMenu() {
		return (
			<>
				<form onSubmit={e => this.handleSubmit(e)}>
					<table>
						<tr><th style={{ "text-align": "center" }}>Timing</th><th>Menu</th></tr>
						<tr><td style={{ "text-align": "center" }}>Breakfast</td><div className="input-field col s12"><textarea onChange={e => this.setState({ breakfast: e.target.value.split(",").map(i => i.trim()) })} id="textarea1" placholder="Insert with with comma" className="materialize-textarea">{this.state.breakfast.join(',')}</textarea></div></tr>
						<tr><td style={{ "text-align": "center" }}>Lunch</td><div className="input-field col s12"><textarea onChange={e => this.setState({ lunch: e.target.value.split(",").map(i => i.trim()) })} id="textarea2" placholder="Insert with with comma" className="materialize-textarea">{this.state.lunch.join(',')}</textarea></div></tr>
						<tr><td style={{ "text-align": "center" }}>Snacks</td><div className="input-field col s12"><textarea onChange={e => this.setState({ snacks: e.target.value.split(",").map(i => i.trim()) })} id="textarea3" placholder="Insert with with comma" className="materialize-textarea">{this.state.snacks.join(',')}</textarea></div></tr>
						<tr><td style={{ "text-align": "center" }}>Dinner</td><div className="input-field col s12"><textarea onChange={e => this.setState({ dinner: e.target.value.split(",").map(i => i.trim()) })} id="textarea4" placholder="Insert with with comma" className="materialize-textarea">{this.state.dinner.join(',')}</textarea></div></tr>
						<tr><td></td><td><input type="submit" className="waves-effect waves-light btn" /></td></tr>
					</table>
				</form>
			</>
		)
	}
	renderVerify() {
		return (
			<>
				<form onSubmit={e => this.handleSubmit2(e)}>
					<table>
						<tr><th style={{ "text-align": "center" }}>Approve</th><th>Name</th><th>Email</th><th>Reg no</th><th>Verification Image</th></tr>
						{this.state.vData.map(i => {
							return (
								<tr>
									<td style={{ "text-align": "center" }}><label><input type="checkbox" onChange={e=>this.handleCheck(e)} defaultChecked={i.verified}/><span></span></label></td>
									<td>{i.name}</td>
									<td>{i.email}</td>
									<td>{i.regno}</td>
									<td><a href={i.img_url}>{i.img_url?"Click here":"One moment"}</a></td>
								</tr>
							)
						})}
						<tr><td style={{ "text-align": "center" }}></td><td></td><td></td><td></td><td><input type="submit" className="waves-effect waves-light btn" /></td></tr>
					</table>
				</form>
			</>
		)
	}
	render() {
		return (
			<div className="App">
				<div className="switch" style={{ margin: "30px" }}>
					<label>Menu<input type="checkbox" onChange={e => this.setState({ isMenu: !this.state.isMenu })} /><span className="lever"></span>Verification</label>
				</div>
				{this.state.isMenu ? this.renderMenu() : this.renderVerify()}
			</div>
		)
	}
}

export default App;
