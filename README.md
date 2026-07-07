
import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  IconButton,
  InputBase,
  Paper,
  Typography,
} from "@mui/material";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";

import { useAuth } from "./AuthContext";

export default function LoginPage() {
  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      await login(form);
      navigate("/", { replace: true });
    } catch {
      setError("Neispravna lozinka ili email");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      component="main"
      sx={{
        minHeight: "100vh",
        width: "100%",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        paddingTop: "64px",
        paddingBottom: "64px",
        backgroundColor: "rgba(37, 37, 37, 0.3)",
        backdropFilter: "blur(8px)",

        // Ako imaš sliku u public folderu, odkomentariši:
        // backgroundImage:
        //   "linear-gradient(rgba(37,37,37,0.3), rgba(37,37,37,0.3)), url('/parking-bg.jpg')",
        // backgroundSize: "cover",
        // backgroundPosition: "center",
        // backgroundRepeat: "no-repeat",
      }}
    >
      <Paper
        component="form"
        onSubmit={handleSubmit}
        elevation={0}
        sx={{
          width: "600px",
          height: "529px",
          display: "flex",
          flexDirection: "column",
          gap: "80px",
          padding: "16px 32px 32px 32px",
          backgroundColor: "rgba(37, 37, 37, 0.85)",
          border: "2px solid #ffffff",
          borderRadius: "16px",
          boxShadow: "0 4px 4px rgba(0, 0, 0, 0.25)",
          color: "#ffffff",
        }}
      >
        <Box
          sx={{
            width: "100%",
            height: "40px",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
          }}
        >
          <Typography
            component="h1"
            sx={{
              margin: 0,
              fontSize: "32px",
              fontWeight: 700,
              color: "#ffffff",
            }}
          >
            Login
          </Typography>
        </Box>

        <Box
          sx={{
            width: "100%",
            height: "164px",
            display: "flex",
            flexDirection: "column",
            gap: "16px",
          }}
        >
          <Box
            sx={{
              width: "100%",
              display: "flex",
              flexDirection: "column",
              gap: "8px",
            }}
          >
            <Typography
              component="label"
              htmlFor="email"
              sx={{
                fontSize: "16px",
                fontWeight: 700,
                color: "#ffffff",
              }}
            >
              E-mail *
            </Typography>

            <InputBase
              id="email"
              type="email"
              value={form.email}
              onChange={(e) =>
                setForm({ ...form, email: e.target.value })
              }
              sx={{
                width: "100%",
                height: "48px",
                border: "2px solid #ffffff",
                borderRadius: "8px",
                backgroundColor: "transparent",
                color: "#ffffff",
                padding: "0 16px",
                fontSize: "16px",

                "&.Mui-focused": {
                  borderColor: "#7EFFC6",
                },

                "& input": {
                  padding: 0,
                  color: "#ffffff",
                },
              }}
            />
          </Box>

          <Box
            sx={{
              width: "100%",
              display: "flex",
              flexDirection: "column",
              gap: "8px",
            }}
          >
            <Typography
              component="label"
              htmlFor="password"
              sx={{
                fontSize: "16px",
                fontWeight: 700,
                color: "#ffffff",
              }}
            >
              Password *
            </Typography>

            <Box
              sx={{
                position: "relative",
                width: "100%",
              }}
            >
              <InputBase
                id="password"
                type={showPassword ? "text" : "password"}
                value={form.password}
                onChange={(e) =>
                  setForm({ ...form, password: e.target.value })
                }
                sx={{
                  width: "100%",
                  height: "48px",
                  border: "2px solid #ffffff",
                  borderRadius: "8px",
                  backgroundColor: "transparent",
                  color: "#ffffff",
                  padding: "0 52px 0 16px",
                  fontSize: "16px",

                  "&.Mui-focused": {
                    borderColor: "#7EFFC6",
                  },

                  "& input": {
                    padding: 0,
                    color: "#ffffff",
                  },
                }}
              />

              <IconButton
                type="button"
                onClick={() => setShowPassword((prev) => !prev)}
                sx={{
                  position: "absolute",
                  top: "50%",
                  right: "12px",
                  transform: "translateY(-50%)",
                  width: "32px",
                  height: "32px",
                  color: "#ffffff",
                  padding: 0,
                }}
              >
                {showPassword ? <VisibilityOff /> : <Visibility />}
              </IconButton>
            </Box>
          </Box>
        </Box>

        <Box
          sx={{
            width: "288px",
            height: "35px",
            alignSelf: "center",
            display: "flex",
            flexDirection: "column",
            gap: "8px",
          }}
        >
          {error && (
            <Typography
              sx={{
                color: "#ff8a8a",
                fontSize: "14px",
                textAlign: "center",
                marginTop: "-40px",
              }}
            >
              {error}
            </Typography>
          )}

          <Button
            type="submit"
            disabled={loading}
            disableElevation
            sx={{
              width: "288px",
              height: "35px",
              maxWidth: "288px",
              borderRadius: "24px",
              padding: "8px 64px",
              backgroundColor: "#7EFFC6",
              color: "#252525",
              fontSize: "16px",
              fontWeight: 500,
              textTransform: "none",
              cursor: "pointer",

              "&:hover": {
                backgroundColor: "#6eeab7",
              },

              "&:disabled": {
                backgroundColor: "#7EFFC6",
                color: "#252525",
                opacity: 0.7,
                cursor: "not-allowed",
              },
            }}
          >
            {loading ? "Loading..." : "Login"}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}
