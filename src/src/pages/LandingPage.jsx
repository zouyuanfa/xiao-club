import { useNavigate } from 'react-router-dom'
import joinNowImg from '../assets/join-now.png'

const LandingPage = () => {
  const navigate = useNavigate()

  const goDetail = () => {
    navigate('/detail')
  }

  return (
    <div className="img-index landing-page">
      <button
        type="button"
        className="landing-action"
        onClick={goDetail}
        aria-label="现在入会"
      >
        <img src={joinNowImg} alt="现在入会" />
      </button>
    </div>
  )
}

export default LandingPage
